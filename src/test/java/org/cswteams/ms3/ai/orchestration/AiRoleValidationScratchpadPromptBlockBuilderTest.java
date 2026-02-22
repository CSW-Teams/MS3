package org.cswteams.ms3.ai.orchestration;

import org.cswteams.ms3.entity.ConcreteShift;
import org.cswteams.ms3.entity.Doctor;
import org.cswteams.ms3.entity.MedicalService;
import org.cswteams.ms3.entity.QuantityShiftSeniority;
import org.cswteams.ms3.entity.Shift;
import org.cswteams.ms3.entity.Task;
import org.cswteams.ms3.enums.Seniority;
import org.cswteams.ms3.enums.SystemActor;
import org.cswteams.ms3.enums.TaskEnum;
import org.cswteams.ms3.enums.TimeSlot;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AiRoleValidationScratchpadPromptBlockBuilderTest {

    private final AiRoleValidationScratchpadPromptBlockBuilder builder = new AiRoleValidationScratchpadPromptBlockBuilder();

    @Test
    void buildRoleValidationScratchpadBlockFiltersCandidatesByExactRoleOnly() {
        LocalDate day = LocalDate.of(2026, 9, 14);
        ConcreteShift shift = new ConcreteShift(day.toEpochDay(), makeShift(1001L, TimeSlot.MORNING,
                Map.of(Seniority.STRUCTURED, 1, Seniority.SPECIALIST_JUNIOR, 1)));

        List<Doctor> doctors = List.of(
                newDoctor(20L, Seniority.SPECIALIST_JUNIOR),
                newDoctor(10L, Seniority.STRUCTURED),
                newDoctor(30L, Seniority.SPECIALIST_SENIOR)
        );

        String block = builder.buildRoleValidationScratchpadBlock(List.of(shift), doctors);

        assertEquals(
                "role_validation_scratchpad[2]{shift_id,required_role,required_count,candidate_doctor_ids}:\n"
                        + "S_1001_20260914,STRUCTURED,1,[10]\n"
                        + "S_1001_20260914,SPECIALIST_JUNIOR,1,[20]\n",
                block
        );
    }

    @Test
    void buildRoleValidationScratchpadBlockUsesDeterministicShiftRoleAndCandidateOrdering() {
        LocalDate firstDay = LocalDate.of(2026, 9, 14);
        LocalDate secondDay = LocalDate.of(2026, 9, 15);

        ConcreteShift laterShift = new ConcreteShift(secondDay.toEpochDay(), makeShift(2002L, TimeSlot.MORNING,
                Map.of(Seniority.SPECIALIST_SENIOR, 1)));
        ConcreteShift firstDayAfternoon = new ConcreteShift(firstDay.toEpochDay(), makeShift(2003L, TimeSlot.AFTERNOON,
                Map.of(Seniority.SPECIALIST_JUNIOR, 1)));
        ConcreteShift firstDayMorning = new ConcreteShift(firstDay.toEpochDay(), makeShift(2001L, TimeSlot.MORNING,
                Map.of(Seniority.STRUCTURED, 1, Seniority.SPECIALIST_SENIOR, 1, Seniority.SPECIALIST_JUNIOR, 1)));

        List<Doctor> doctors = List.of(
                newDoctor(7L, Seniority.SPECIALIST_SENIOR),
                newDoctor(3L, Seniority.STRUCTURED),
                newDoctor(9L, Seniority.SPECIALIST_SENIOR),
                newDoctor(2L, Seniority.SPECIALIST_JUNIOR)
        );

        String block = builder.buildRoleValidationScratchpadBlock(
                List.of(laterShift, firstDayAfternoon, firstDayMorning),
                doctors
        );

        assertEquals(
                "role_validation_scratchpad[5]{shift_id,required_role,required_count,candidate_doctor_ids}:\n"
                        + "S_2001_20260914,STRUCTURED,1,[3]\n"
                        + "S_2001_20260914,SPECIALIST_JUNIOR,1,[2]\n"
                        + "S_2001_20260914,SPECIALIST_SENIOR,1,[7,9]\n"
                        + "S_2003_20260914,SPECIALIST_JUNIOR,1,[2]\n"
                        + "S_2002_20260915,SPECIALIST_SENIOR,1,[7,9]\n",
                block
        );
    }

    @Test
    void buildRoleValidationScratchpadBlockKeepsRolesWithNoCandidatesAsExplicitEmptyLists() {
        LocalDate day = LocalDate.of(2026, 9, 14);
        ConcreteShift shift = new ConcreteShift(day.toEpochDay(), makeShift(3001L, TimeSlot.NIGHT,
                Map.of(Seniority.SPECIALIST_SENIOR, 2)));

        String block = builder.buildRoleValidationScratchpadBlock(
                List.of(shift),
                List.of(newDoctor(11L, Seniority.STRUCTURED))
        );

        assertEquals(
                "role_validation_scratchpad[1]{shift_id,required_role,required_count,candidate_doctor_ids}:\n"
                        + "S_3001_20260914,SPECIALIST_SENIOR,2,[]\n",
                block
        );
    }

    private Shift makeShift(Long id, TimeSlot timeSlot, Map<Seniority, Integer> seniorityMap) {
        Task task = new Task(TaskEnum.CLINIC);
        MedicalService service = new MedicalService(List.of(task), "Ward");
        QuantityShiftSeniority quantity = new QuantityShiftSeniority(seniorityMap, task);
        return new Shift(
                id,
                timeSlot,
                LocalTime.of(8, 0),
                Duration.ofHours(6),
                Set.of(DayOfWeek.MONDAY),
                service,
                List.of(quantity),
                List.of()
        );
    }

    private Doctor newDoctor(Long id, Seniority seniority) {
        Doctor doctor = new Doctor(
                "Mario",
                "Rossi",
                "TAXCODEX00000000",
                LocalDate.of(1980, 1, 1),
                "doctor@example.com",
                "secret",
                seniority,
                Set.of(SystemActor.DOCTOR)
        );
        setField(doctor, "id", id);
        return doctor;
    }

    private void setField(Object target, String fieldName, Object value) {
        try {
            Field field = target.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(target, value);
        } catch (Exception ex) {
            throw new IllegalStateException("Unable to set field " + fieldName, ex);
        }
    }
}
