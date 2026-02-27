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
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class AiRoleValidationScratchpadPromptBlockBuilderTest {

    @Test
    void buildRoleValidationScratchpadBlockKeepsDoctorMembershipAfterShuffle() {
        AiRoleValidationScratchpadPromptBlockBuilder builder = new AiRoleValidationScratchpadPromptBlockBuilder();
        LocalDate day = LocalDate.of(2026, 9, 14);
        ConcreteShift shift = new ConcreteShift(day.toEpochDay(), makeShift(1001L, TimeSlot.MORNING,
                Map.of(Seniority.SPECIALIST_SENIOR, 1)));

        List<Doctor> doctors = List.of(
                newDoctor(30L, Seniority.SPECIALIST_SENIOR),
                newDoctor(10L, Seniority.SPECIALIST_SENIOR),
                newDoctor(20L, Seniority.SPECIALIST_SENIOR),
                newDoctor(40L, Seniority.SPECIALIST_SENIOR)
        );

        String block = builder.buildRoleValidationScratchpadBlock(List.of(shift), doctors);

        assertEquals(Set.of(10L, 20L, 30L, 40L), parseDoctorIds(block, "SPECIALIST_SENIOR"));
    }

    @Test
    void buildRoleValidationScratchpadBlockCanProduceDifferentOrderingAcrossRetries() {
        AtomicLong seed = new AtomicLong(1L);
        Supplier<Random> varyingRandomSupplier = () -> new Random(seed.getAndIncrement());
        AiRoleValidationScratchpadPromptBlockBuilder builder =
                new AiRoleValidationScratchpadPromptBlockBuilder(varyingRandomSupplier);
        LocalDate day = LocalDate.of(2026, 9, 14);
        ConcreteShift shift = new ConcreteShift(day.toEpochDay(), makeShift(1002L, TimeSlot.MORNING,
                Map.of(Seniority.SPECIALIST_SENIOR, 1)));

        List<Doctor> doctors = List.of(
                newDoctor(10L, Seniority.SPECIALIST_SENIOR),
                newDoctor(20L, Seniority.SPECIALIST_SENIOR),
                newDoctor(30L, Seniority.SPECIALIST_SENIOR),
                newDoctor(40L, Seniority.SPECIALIST_SENIOR)
        );

        String firstBlock = builder.buildRoleValidationScratchpadBlock(List.of(shift), doctors);
        String secondBlock = builder.buildRoleValidationScratchpadBlock(List.of(shift), doctors);

        assertNotEquals(parseDoctorIdOrder(firstBlock, "SPECIALIST_SENIOR"),
                parseDoctorIdOrder(secondBlock, "SPECIALIST_SENIOR"));
    }

    @Test
    void buildRoleValidationScratchpadBlockUsesDeterministicOrderingWithSeededRandom() {
        AiRoleValidationScratchpadPromptBlockBuilder builder =
                new AiRoleValidationScratchpadPromptBlockBuilder(() -> new Random(42L));
        LocalDate day = LocalDate.of(2026, 9, 14);
        ConcreteShift shift = new ConcreteShift(day.toEpochDay(), makeShift(1003L, TimeSlot.MORNING,
                Map.of(Seniority.SPECIALIST_SENIOR, 1)));

        List<Doctor> doctors = List.of(
                newDoctor(10L, Seniority.SPECIALIST_SENIOR),
                newDoctor(20L, Seniority.SPECIALIST_SENIOR),
                newDoctor(30L, Seniority.SPECIALIST_SENIOR),
                newDoctor(40L, Seniority.SPECIALIST_SENIOR)
        );

        String firstBlock = builder.buildRoleValidationScratchpadBlock(List.of(shift), doctors);
        String secondBlock = builder.buildRoleValidationScratchpadBlock(List.of(shift), doctors);

        assertEquals(parseDoctorIdOrder(firstBlock, "SPECIALIST_SENIOR"),
                parseDoctorIdOrder(secondBlock, "SPECIALIST_SENIOR"));
        assertEquals(List.of(40L, 20L, 10L, 30L), parseDoctorIdOrder(firstBlock, "SPECIALIST_SENIOR"));
    }

    private List<Long> parseDoctorIdOrder(String block, String role) {
        String[] lines = block.split("\\n");
        for (String line : lines) {
            if (!line.contains("," + role + ",")) {
                continue;
            }
            int openBracket = line.lastIndexOf('[');
            int closeBracket = line.lastIndexOf(']');
            if (openBracket < 0 || closeBracket < openBracket + 1) {
                return List.of();
            }
            String content = line.substring(openBracket + 1, closeBracket);
            if (content.isBlank()) {
                return List.of();
            }
            return Arrays.stream(content.split(","))
                    .map(String::trim)
                    .map(Long::parseLong)
                    .toList();
        }
        throw new IllegalStateException("Role not found in block: " + role);
    }

    private Set<Long> parseDoctorIds(String block, String role) {
        return Set.copyOf(parseDoctorIdOrder(block, role));
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
