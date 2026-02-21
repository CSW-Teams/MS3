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

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

    @Test
    void buildsCompactPayloadForAiWithEquivalentRequiredDataAndSmallerOutput() {
        LocalDate periodStart = LocalDate.of(2026, 5, 20);
        LocalDate periodEnd = LocalDate.of(2026, 5, 22);
        Doctor doctor = newDoctor(10L, Seniority.STRUCTURED);

        DoctorUffaPriority priority = new DoctorUffaPriority(doctor);
        priority.setGeneralPriority(11);
        priority.setNightPriority(13);
        priority.setLongShiftPriority(7);

        doctor.getPreferenceList().add(new Preference(periodStart, Set.of(TimeSlot.MORNING, TimeSlot.NIGHT), List.of(doctor)));
        doctor.getPreferenceList().add(new Preference(periodStart.plusDays(1), Set.of(TimeSlot.MORNING, TimeSlot.NIGHT), List.of(doctor)));
        doctor.getPreferenceList().add(new Preference(periodStart.plusDays(2), Set.of(TimeSlot.AFTERNOON), List.of(doctor)));

        Shift shift = makeShift(
                101L,
                TimeSlot.NIGHT,
                LocalTime.of(20, 0),
                Duration.ofMinutes(720)
        );
        ConcreteShift concreteShift = new ConcreteShift(periodStart.toEpochDay(), shift);
        String shiftId = ToonBuilder.shiftIdFor(concreteShift);

        ToonActiveConstraint emptyParamsConstraint = new ToonActiveConstraint(
                ToonConstraintType.HARD,
                ToonConstraintEntityType.DOCTOR,
                String.valueOf(doctor.getId()),
                "MAX_CONSECUTIVE_NIGHTS",
                Map.of()
        );

        Holiday laterHoliday = new Holiday();
        laterHoliday.setId(200L);
        laterHoliday.setStartDate(periodStart.plusDays(2));
        laterHoliday.setEndDate(periodStart.plusDays(2));

        Holiday earlierHoliday = new Holiday();
        earlierHoliday.setId(100L);
        earlierHoliday.setStartDate(periodStart);
        earlierHoliday.setEndDate(periodStart.plusDays(1));
        earlierHoliday.setLocation("Europe/Rome");

        DoctorHolidays doctorHolidays = new DoctorHolidays(doctor, new HashMap<>(Map.of(
                laterHoliday, true,
                earlierHoliday, true
        )));

        ToonRequestContext context = new ToonRequestContext(
                periodStart,
                periodEnd,
                "generate",
                List.of(concreteShift),
                List.of(doctor),
                List.of(priority),
                List.of(doctorHolidays),
                List.of(emptyParamsConstraint),
                List.of()
        );

        ToonBuilder builder = new ToonBuilder();
        String legacy = builder.build(context);
        String compact = assertDoesNotThrow(() -> builder.build(context, ToonBuilder.SerializationMode.COMPACT));

        assertTrue(compact.length() < legacy.length());
        assertFalse(compact.contains("#"));
        assertFalse(compact.contains("feedbacks["));
        assertFalse(compact.contains("fb["));

        assertTrue(compact.contains("ctx:{p:\"2026-05-20/2026-05-22\",m:\"generate\",hv:2}"));
        assertTrue(compact.contains("sh[1]{i,s,d,u,rs,rj}:"));
        assertTrue(compact.contains(shiftId + ",NIGHT,2026-05-20,720,1,0"));
        assertTrue(compact.contains("-i:10"));
        assertTrue(compact.contains("r:STRUCTURED"));
        assertTrue(compact.contains("pr:11,13,7"));
        assertTrue(compact.contains("h[2]{id,s,e,tz?}:"));
        assertTrue(compact.contains("-100,2026-05-20,2026-05-21,\"Europe/Rome\""));
        assertTrue(compact.contains("-200,2026-05-22,2026-05-22"));
        assertTrue(compact.contains("b[2]{s,e,t}:"));
        assertTrue(compact.contains("-2026-05-20,2026-05-21,[\"MORNING\",\"NIGHT\"]"));
        assertTrue(compact.contains("-2026-05-22,2026-05-22,[\"AFTERNOON\"]"));
        assertTrue(compact.contains("ac[1]{t,e,i,r,p}:\nHARD,DOCTOR,10,MAX_CONSECUTIVE_NIGHTS\n"));

        assertTrue(legacy.contains("feedbacks[0]") || !legacy.contains("feedbacks["));
        assertTrue(legacy.contains(shiftId));
        assertTrue(legacy.contains("doctors[1]:"));
    }

    @Test
    void compactShiftRequirementsKeepSeniorCountsOutOfRj() {
        LocalDate periodStart = LocalDate.of(2026, 9, 1);
        Doctor doctor = newDoctor(10L, Seniority.STRUCTURED);

        Task task = new Task(TaskEnum.CLINIC);
        MedicalService service = new MedicalService(List.of(task), "Ward");
        QuantityShiftSeniority quantity = new QuantityShiftSeniority(Map.of(
                Seniority.STRUCTURED, 1,
                Seniority.SPECIALIST_JUNIOR, 2,
                Seniority.SPECIALIST_SENIOR, 3
        ), task);

        Shift shift = new Shift(
                201L,
                TimeSlot.MORNING,
                LocalTime.of(8, 0),
                Duration.ofMinutes(360),
                Set.of(DayOfWeek.TUESDAY),
                service,
                List.of(quantity),
                List.of()
        );

        ConcreteShift concreteShift = new ConcreteShift(periodStart.toEpochDay(), shift);

        ToonRequestContext context = new ToonRequestContext(
                periodStart,
                periodStart,
                "generate",
                List.of(concreteShift),
                List.of(doctor),
                List.of(new DoctorUffaPriority(doctor)),
                List.of(),
                List.of(),
                List.of()
        );

        ToonBuilder builder = new ToonBuilder();
        String compact = builder.build(context, ToonBuilder.SerializationMode.COMPACT);

        assertTrue(compact.contains("S_201_20260901,MORNING,2026-09-01,360,1,2"));
    }

    @Test
    void buildsCompactPayloadWithStableSectionOrderAndExplicitHolidaySchema() {
        LocalDate periodStart = LocalDate.of(2026, 8, 10);
        LocalDate periodEnd = LocalDate.of(2026, 8, 12);

        Doctor seniorDoctor = newDoctor(20L, Seniority.SPECIALIST_SENIOR);
        Doctor structuredDoctor = newDoctor(10L, Seniority.STRUCTURED);

        DoctorUffaPriority seniorPriority = new DoctorUffaPriority(seniorDoctor);
        seniorPriority.setGeneralPriority(8);
        seniorPriority.setNightPriority(6);
        seniorPriority.setLongShiftPriority(4);

        DoctorUffaPriority structuredPriority = new DoctorUffaPriority(structuredDoctor);
        structuredPriority.setGeneralPriority(3);
        structuredPriority.setNightPriority(2);
        structuredPriority.setLongShiftPriority(1);

        Holiday holidayWithoutId = new Holiday();
        holidayWithoutId.setStartDate(periodStart.plusDays(1));
        holidayWithoutId.setEndDate(periodStart.plusDays(1));

        Holiday holidayWithTimezone = new Holiday();
        holidayWithTimezone.setId(15L);
        holidayWithTimezone.setStartDate(periodStart);
        holidayWithTimezone.setEndDate(periodStart.plusDays(2));
        holidayWithTimezone.setLocation("Europe/Rome");

        DoctorHolidays structuredHolidays = new DoctorHolidays(structuredDoctor, new HashMap<>(Map.of(
                holidayWithoutId, true,
                holidayWithTimezone, true
        )));

        DoctorHolidays seniorHolidays = new DoctorHolidays(seniorDoctor, new HashMap<>(Map.of()));

        ConcreteShift morningShift = new ConcreteShift(periodStart.toEpochDay(), makeShift(
                301L,
                TimeSlot.MORNING,
                LocalTime.of(8, 0),
                Duration.ofMinutes(360)
        ));

        ConcreteShift afternoonShift = new ConcreteShift(periodStart.toEpochDay(), makeShift(
                302L,
                TimeSlot.AFTERNOON,
                LocalTime.of(14, 0),
                Duration.ofMinutes(360)
        ));

        ToonActiveConstraint hardConstraint = new ToonActiveConstraint(
                ToonConstraintType.HARD,
                ToonConstraintEntityType.DOCTOR,
                String.valueOf(structuredDoctor.getId()),
                "STABLE_ORDER",
                Map.of("level", "strict")
        );

        ToonRequestContext context = new ToonRequestContext(
                periodStart,
                periodEnd,
                "generate",
                List.of(afternoonShift, morningShift),
                List.of(seniorDoctor, structuredDoctor),
                List.of(seniorPriority, structuredPriority),
                List.of(structuredHolidays, seniorHolidays),
                List.of(hardConstraint),
                List.of()
        );

        ToonBuilder builder = new ToonBuilder();
        String compact = builder.build(context, ToonBuilder.SerializationMode.COMPACT);

        String expectedSnapshot = "ctx:{p:\"2026-08-10/2026-08-12\",m:\"generate\",hv:2}\n"
                + "sh[2]{i,s,d,u,rs,rj}:\n"
                + "S_302_20260810,AFTERNOON,2026-08-10,360,1,0\n"
                + "S_301_20260810,MORNING,2026-08-10,360,1,0\n"
                + "dr[2]:\n"
                + "-i:10\n"
                + " r:STRUCTURED\n"
                + " pr:3,2,1\n"
                + " h[2]{id,s,e,tz?}:\n"
                + "  -15,2026-08-10,2026-08-12,\"Europe/Rome\"\n"
                + "  -,2026-08-11,2026-08-11\n"
                + "-i:20\n"
                + " r:SPECIALIST_SENIOR\n"
                + " pr:8,6,4\n"
                + " h[0]{id,s,e,tz?}:\n"
                + "ac[1]{t,e,i,r,p}:\n"
                + "HARD,DOCTOR,10,STABLE_ORDER,{level:\"strict\"}\n";

        assertEquals(expectedSnapshot, compact);
    }

    @Test
    void includesAllMappedConstraintsInActiveConstraintsSection() {
        LocalDate periodStart = LocalDate.of(2026, 6, 1);
        LocalDate periodEnd = LocalDate.of(2026, 6, 1);
        Doctor doctor = newDoctor(44L, Seniority.STRUCTURED);
        Shift shift = makeShift(
                401L,
                TimeSlot.AFTERNOON,
                LocalTime.of(14, 0),
                Duration.ofMinutes(360)
        );
        ConcreteShift concreteShift = new ConcreteShift(periodStart.toEpochDay(), shift);
        String shiftId = ToonBuilder.shiftIdFor(concreteShift);

        ToonActiveConstraint hardDoctorConstraint = new ToonActiveConstraint(
                ToonConstraintType.HARD,
                ToonConstraintEntityType.DOCTOR,
                String.valueOf(doctor.getId()),
                "MAX_HOURS",
                Map.of("limit", "480")
        );
        ToonActiveConstraint softShiftConstraint = new ToonActiveConstraint(
                ToonConstraintType.SOFT,
                ToonConstraintEntityType.SHIFT,
                shiftId,
                "ROLE_QUOTA",
                Map.of("structured", "1")
        );

        ToonRequestContext context = new ToonRequestContext(
                periodStart,
                periodEnd,
                "generate",
                List.of(concreteShift),
                List.of(doctor),
                List.of(new DoctorUffaPriority(doctor)),
                List.of(),
                List.of(hardDoctorConstraint, softShiftConstraint),
                List.of()
        );

        ToonBuilder builder = new ToonBuilder();
        String legacy = builder.build(context);
        String compact = builder.build(context, ToonBuilder.SerializationMode.COMPACT);

        assertTrue(legacy.contains("active_constraints[2]{type, entity_type, entity_id, reason, params}:"));
        assertTrue(legacy.contains("HARD, DOCTOR, 44, MAX_HOURS, { \"limit\": \"480\" }"));
        assertTrue(legacy.contains("SOFT, SHIFT, " + shiftId + ", ROLE_QUOTA, { \"structured\": \"1\" }"));

        assertTrue(compact.contains("ac[2]{t,e,i,r,p}:"));
        assertTrue(compact.contains("HARD,DOCTOR,44,MAX_HOURS,{limit:\"480\"}"));
        assertTrue(compact.contains("SOFT,SHIFT," + shiftId + ",ROLE_QUOTA,{structured:\"1\"}"));
    }

    @Test
    void emitsCanonicalSpecialistRoleNamesInToonPayload() {
        LocalDate periodStart = LocalDate.of(2026, 5, 20);
        LocalDate periodEnd = LocalDate.of(2026, 5, 20);
        Doctor juniorDoctor = newDoctor(20L, Seniority.SPECIALIST_JUNIOR);
        Doctor seniorDoctor = newDoctor(21L, Seniority.SPECIALIST_SENIOR);
        Shift shift = makeShift(
                120L,
                TimeSlot.MORNING,
                LocalTime.of(8, 0),
                Duration.ofMinutes(360)
        );
        ConcreteShift concreteShift = new ConcreteShift(periodStart.toEpochDay(), shift);

        ToonRequestContext context = new ToonRequestContext(
                periodStart,
                periodEnd,
                "generate",
                List.of(concreteShift),
                List.of(juniorDoctor, seniorDoctor),
                List.of(new DoctorUffaPriority(juniorDoctor), new DoctorUffaPriority(seniorDoctor)),
                List.of(),
                List.of(),
                List.of()
        );

        ToonBuilder builder = new ToonBuilder();
        String legacy = builder.build(context);
        String compact = builder.build(context, ToonBuilder.SerializationMode.COMPACT);

        assertTrue(legacy.contains("  role: SPECIALIST_JUNIOR"));
        assertTrue(legacy.contains("  role: SPECIALIST_SENIOR"));
        assertFalse(legacy.contains("  role: JUNIOR"));

        assertTrue(compact.contains("r:SPECIALIST_JUNIOR"));
        assertTrue(compact.contains("r:SPECIALIST_SENIOR"));
        assertFalse(compact.contains("r:JUNIOR"));
    }


    @Test
    void compactCtxAndShiftSchemaExposeSerializerSemantics() {
        LocalDate day = LocalDate.of(2026, 9, 3);
        Doctor doctor = newDoctor(11L, Seniority.STRUCTURED);

        Task task = new Task(TaskEnum.CLINIC);
        MedicalService service = new MedicalService(List.of(task), "Ward");
        QuantityShiftSeniority quantity = new QuantityShiftSeniority(Map.of(
                Seniority.STRUCTURED, 2,
                Seniority.SPECIALIST_JUNIOR, 1,
                Seniority.SPECIALIST_SENIOR, 4
        ), task);

        Shift shift = new Shift(
                501L,
                TimeSlot.NIGHT,
                LocalTime.of(20, 0),
                Duration.ofMinutes(720),
                Set.of(DayOfWeek.THURSDAY),
                service,
                List.of(quantity),
                List.of()
        );

        String compact = new ToonBuilder().build(new ToonRequestContext(
                day,
                day,
                "generate",
                List.of(new ConcreteShift(day.toEpochDay(), shift)),
                List.of(doctor),
                List.of(new DoctorUffaPriority(doctor)),
                List.of(),
                List.of(),
                List.of()
        ), ToonBuilder.SerializationMode.COMPACT);

        assertTrue(compact.startsWith("ctx:{p:\"2026-09-03/2026-09-03\",m:\"generate\",hv:2}"));
        assertTrue(compact.contains("sh[1]{i,s,d,u,rs,rj}:"));
        assertTrue(compact.contains("S_501_20260903,NIGHT,2026-09-03,720,2,1"));
        assertFalse(compact.contains("S_501_20260903,NIGHT,2026-09-03,720,2,5"));
    }

    @Test
    void compactDoctorHolidayRowsAreStructuredAndDeterministicallyOrdered() {
        LocalDate day = LocalDate.of(2026, 10, 1);
        Doctor doctor = newDoctor(42L, Seniority.STRUCTURED);

        Holiday nullIdEarlier = new Holiday();
        nullIdEarlier.setStartDate(day);
        nullIdEarlier.setEndDate(day.plusDays(1));

        Holiday id5Earlier = new Holiday();
        id5Earlier.setId(5L);
        id5Earlier.setStartDate(day);
        id5Earlier.setEndDate(day.plusDays(2));

        Holiday id2Later = new Holiday();
        id2Later.setId(2L);
        id2Later.setStartDate(day.plusDays(1));
        id2Later.setEndDate(day.plusDays(1));
        id2Later.setLocation("Europe/Rome");

        DoctorHolidays doctorHolidays = new DoctorHolidays(doctor, new HashMap<>(Map.of(
                id2Later, true,
                nullIdEarlier, true,
                id5Earlier, true
        )));

        String compact = new ToonBuilder().build(new ToonRequestContext(
                day,
                day.plusDays(1),
                "generate",
                List.of(new ConcreteShift(day.toEpochDay(), makeShift(502L, TimeSlot.MORNING, LocalTime.of(8, 0), Duration.ofMinutes(360)))),
                List.of(doctor),
                List.of(new DoctorUffaPriority(doctor)),
                List.of(doctorHolidays),
                List.of(),
                List.of()
        ), ToonBuilder.SerializationMode.COMPACT);

        String expectedHolidayBlock = " h[3]{id,s,e,tz?}:\n"
                + "  -5,2026-10-01,2026-10-03\n"
                + "  -,2026-10-01,2026-10-02\n"
                + "  -2,2026-10-02,2026-10-02,\"Europe/Rome\"\n";

        assertTrue(compact.contains("dr[1]:\n"));
        assertTrue(compact.contains(expectedHolidayBlock));
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
