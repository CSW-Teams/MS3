package org.cswteams.ms3.control.toon;

import org.cswteams.ms3.entity.ConcreteShift;
import org.cswteams.ms3.entity.Doctor;
import org.cswteams.ms3.entity.DoctorHolidays;
import org.cswteams.ms3.entity.DoctorUffaPriority;
import org.cswteams.ms3.entity.Holiday;
import org.cswteams.ms3.entity.MedicalService;
import org.cswteams.ms3.entity.Preference;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ToonBuilderTest {

    @Test
    void buildsDeterministicToonPayload() {
        LocalDate periodStart = LocalDate.of(2026, 5, 20);
        LocalDate periodEnd = LocalDate.of(2026, 5, 21);
        Doctor doctor = newDoctor(10L, Seniority.STRUCTURED);
        DoctorUffaPriority priority = new DoctorUffaPriority(doctor);
        priority.setGeneralPriority(11);
        priority.setNightPriority(13);
        priority.setLongShiftPriority(7);

        Holiday holiday = new Holiday();
        holiday.setId(99L);
        DoctorHolidays doctorHolidays = new DoctorHolidays(doctor, new HashMap<>(Map.of(holiday, true)));

        Preference preference = new Preference(periodStart, Set.of(TimeSlot.MORNING, TimeSlot.NIGHT), List.of(doctor));
        doctor.getPreferenceList().add(preference);

        Task task = new Task(TaskEnum.CLINIC);
        MedicalService service = new MedicalService(List.of(task), "Ward");
        QuantityShiftSeniority quantity = new QuantityShiftSeniority(Map.of(
                Seniority.STRUCTURED, 2,
                Seniority.SPECIALIST_JUNIOR, 1
        ), task);
        Shift shift = new Shift(
                101L,
                TimeSlot.NIGHT,
                LocalTime.of(20, 0),
                Duration.ofMinutes(720),
                Set.of(DayOfWeek.MONDAY),
                service,
                List.of(quantity),
                List.of()
        );

        ConcreteShift concreteShift = new ConcreteShift(periodStart.toEpochDay(), shift);

        ToonActiveConstraint constraint = new ToonActiveConstraint(
                ToonConstraintType.HARD,
                ToonConstraintEntityType.DOCTOR,
                String.valueOf(doctor.getId()),
                "REST_PERIOD",
                Map.of("until", "2026-05-21T08:00:00Z")
        );

        String shiftId = ToonBuilder.shiftIdFor(concreteShift);
        ToonFeedback feedback = new ToonFeedback(shiftId, doctor.getId(), "TOO_MANY_NIGHTS", 4);

        ToonRequestContext context = new ToonRequestContext(
                periodStart,
                periodEnd,
                "rebalance_uffa",
                List.of(concreteShift),
                List.of(doctor),
                List.of(priority),
                List.of(doctorHolidays),
                List.of(constraint),
                List.of(feedback)
        );

        ToonBuilder builder = new ToonBuilder();
        String output = builder.build(context);
        assertNotNull(output);
        String expected = "# Metadati della sessione\n" +
                "ctx:\n" +
                "period: \"2026-05-20/2026-05-21\"\n" +
                "mode: \"rebalance_uffa\"\n\n" +
                "# Catalogo Turni (ConcreteShifts da coprire)\n" +
                "# Formato Tabellare per efficienza token\n" +
                "shifts[1]{id, slot, date, duration, req_str, req_jun}:\n" +
                shiftId + ", NIGHT, 2026-05-20, 720, 2, 1\n\n" +
                "# Registro Medici\n" +
                "doctors[1]:\n" +
                "- id: 10\n" +
                "  role: STRUCTURED\n" +
                "  priorities{gen, night, long}: 11, 13, 7\n" +
                "  holidays_taken[1]: \"HOLIDAY_99\"\n" +
                "  blocks[1]:\n" +
                "  - start: 2026-05-20, end: 2026-05-20, slots: [\"MORNING\", \"NIGHT\"]\n\n" +
                "# Feedbacks\n" +
                "feedbacks[1]{shift_id, doctor_id, reason_code, severity, comment}:\n" +
                shiftId + ", 10, TOO_MANY_NIGHTS, 4, \"\"\n\n" +
                "# Vincoli di Business\n" +
                "active_constraints[1]{type, entity_type, entity_id, reason, params}:\n" +
                "HARD, DOCTOR, 10, REST_PERIOD, { \"until\": \"2026-05-21T08:00:00Z\" }\n";
        assertEquals(expected, output);
    }

    @Test
    void failsPreValidationOnInvalidFeedbackShift() {
        LocalDate periodStart = LocalDate.of(2026, 5, 20);
        LocalDate periodEnd = LocalDate.of(2026, 5, 21);
        Doctor doctor = newDoctor(1L, Seniority.SPECIALIST_JUNIOR);

        Shift shift = makeShift(
                1L,
                TimeSlot.MORNING,
                LocalTime.of(8, 0),
                Duration.ofMinutes(360)
        );
        ConcreteShift concreteShift = new ConcreteShift(periodStart.toEpochDay(), shift);

        ToonFeedback feedback = new ToonFeedback("S_UNKNOWN", doctor.getId(), "TOO_LONG", 3);

        ToonRequestContext context = new ToonRequestContext(
                periodStart,
                periodEnd,
                "rebalance_uffa",
                List.of(concreteShift),
                List.of(doctor),
                List.of(),
                List.of(),
                List.of(),
                List.of(feedback)
        );

        ToonBuilder builder = new ToonBuilder();
        assertThrows(ToonValidationException.class, () -> builder.build(context));
    }

    @Test
    void detectsMissingDoctorPriorityReference() {
        LocalDate periodStart = LocalDate.of(2026, 5, 20);
        LocalDate periodEnd = LocalDate.of(2026, 5, 21);
        Doctor doctor = newDoctor(2L, Seniority.SPECIALIST_SENIOR);

        Shift shift = makeShift(
                2L,
                TimeSlot.AFTERNOON,
                LocalTime.of(14, 0),
                Duration.ofMinutes(360)
        );
        ConcreteShift concreteShift = new ConcreteShift(periodStart.toEpochDay(), shift);

        DoctorUffaPriority priority = new DoctorUffaPriority();
        priority.setDoctor(null);

        ToonRequestContext context = new ToonRequestContext(
                periodStart,
                periodEnd,
                "rebalance_uffa",
                List.of(concreteShift),
                List.of(doctor),
                List.of(priority),
                List.of(),
                List.of(),
                List.of()
        );

        ToonBuilder builder = new ToonBuilder();
        assertThrows(ToonValidationException.class, () -> builder.build(context));
    }

    @Test
    void excludesPiiFieldsFromPayload() {
        LocalDate periodStart = LocalDate.of(2026, 5, 20);
        LocalDate periodEnd = LocalDate.of(2026, 5, 21);
        Doctor doctor = newDoctor(3L, Seniority.STRUCTURED);

        Shift shift = makeShift(
                3L,
                TimeSlot.MORNING,
                LocalTime.of(8, 0),
                Duration.ofMinutes(360)
        );
        ConcreteShift concreteShift = new ConcreteShift(periodStart.toEpochDay(), shift);

        ToonRequestContext context = new ToonRequestContext(
                periodStart,
                periodEnd,
                "rebalance_uffa",
                List.of(concreteShift),
                List.of(doctor),
                List.of(new DoctorUffaPriority(doctor)),
                List.of(),
                List.of(),
                List.of()
        );

        ToonBuilder builder = new ToonBuilder();
        String output = builder.build(context);

        assertFalse(output.contains("doctor@example.com"));
        assertFalse(output.contains("Mario"));
        assertFalse(output.contains("Rossi"));
        assertFalse(output.contains("@"));
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

    private Shift makeShift(Long id, TimeSlot timeSlot, LocalTime startTime, Duration duration) {
        Task task = new Task(TaskEnum.CLINIC);
        MedicalService service = new MedicalService(List.of(task), "Ward");
        QuantityShiftSeniority quantity = new QuantityShiftSeniority(Map.of(
                Seniority.STRUCTURED, 1
        ), task);
        return new Shift(
                id,
                timeSlot,
                startTime,
                duration,
                Set.of(DayOfWeek.MONDAY),
                service,
                List.of(quantity),
                List.of()
        );
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
