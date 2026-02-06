package org.cswteams.ms3.ai.orchestration;

import org.cswteams.ms3.control.toon.ToonActiveConstraint;
import org.cswteams.ms3.control.toon.ToonBuilder;
import org.cswteams.ms3.control.toon.ToonConstraintEntityType;
import org.cswteams.ms3.control.toon.ToonConstraintType;
import org.cswteams.ms3.control.toon.ToonFeedback;
import org.cswteams.ms3.control.toon.ToonRequestContext;
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
import static org.junit.jupiter.api.Assertions.assertTrue;

class AiReschedulingOrchestrationServiceTest {

    @Test
    void pseudonymizesDoctorIdsInToonPayload() {
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

        Shift shift = makeShift(
                101L,
                TimeSlot.NIGHT,
                LocalTime.of(20, 0),
                Duration.ofMinutes(720)
        );
        ConcreteShift concreteShift = new ConcreteShift(periodStart.toEpochDay(), shift);
        String shiftId = ToonBuilder.shiftIdFor(concreteShift);

        ToonFeedback feedback = new ToonFeedback(shiftId, doctor.getId(), "TOO_MANY_NIGHTS", 4);
        ToonActiveConstraint constraint = new ToonActiveConstraint(
                ToonConstraintType.HARD,
                ToonConstraintEntityType.DOCTOR,
                String.valueOf(doctor.getId()),
                "REST_PERIOD",
                Map.of("until", "2026-05-21T08:00:00Z")
        );

        AiReschedulingOrchestrationService service = new AiReschedulingOrchestrationService();
        AiReschedulingToonRequest toonRequest = service.buildToonRequestContext(
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

        ToonRequestContext context = toonRequest.getToonRequestContext();
        assertEquals(1, context.getDoctors().size());
        assertEquals(1L, context.getDoctors().get(0).getId());

        Map<Long, Long> reverseMapping = toonRequest.getPseudonymToDoctorId();
        assertEquals(10L, reverseMapping.get(1L));

        ToonBuilder builder = new ToonBuilder();
        String payload = builder.build(context);
        assertNotNull(payload);
        assertTrue(payload.contains("- id: 1\n"));
        assertFalse(payload.contains("- id: 10\n"));
        assertTrue(payload.contains(shiftId + ", 1, TOO_MANY_NIGHTS, 4\n"));
        assertTrue(payload.contains("HARD, DOCTOR, 1, REST_PERIOD, { \"until\": \"2026-05-21T08:00:00Z\" }\n"));
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
                Seniority.STRUCTURED, 2,
                Seniority.SPECIALIST_JUNIOR, 1
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
