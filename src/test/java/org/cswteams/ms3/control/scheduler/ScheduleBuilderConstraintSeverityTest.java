package org.cswteams.ms3.control.scheduler;

import org.cswteams.ms3.entity.*;
import org.cswteams.ms3.entity.constraint.Constraint;
import org.cswteams.ms3.entity.constraint.ConstraintHoliday;
import org.cswteams.ms3.entity.constraint.ConstraintTurniContigui;
import org.cswteams.ms3.entity.constraint.ContextConstraint;
import org.cswteams.ms3.enums.*;
import org.cswteams.ms3.exception.IllegalScheduleException;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class ScheduleBuilderConstraintSeverityTest {

    @Test
    void softTurniContiguiViolationIsNonBlockingInHardOnlyMode() throws Exception {
        Doctor doctor = buildDoctor();
        Schedule schedule = new Schedule(0L, 10L, new ArrayList<>());
        DoctorUffaPriority dup = new DoctorUffaPriority(doctor, schedule);

        ConcreteShift existingNightShift = new ConcreteShift(5L, buildShift(TimeSlot.NIGHT, LocalTime.of(20, 0)));
        dup.setAssegnazioniTurnoCache(new ArrayList<>(Collections.singletonList(existingNightShift)));

        ConstraintTurniContigui softConstraint = new ConstraintTurniContigui(
                12,
                ChronoUnit.HOURS,
                TimeSlot.NIGHT,
                new HashSet<>(Arrays.asList(TimeSlot.values()))
        );
        softConstraint.setDescription("Turni contigui soft");
        softConstraint.setViolable(true);

        ScheduleBuilder scheduleBuilder = buildScheduleBuilder(schedule, dup, Collections.singletonList(softConstraint));
        ContextConstraint context = new ContextConstraint(dup, new ConcreteShift(6L, buildShift(TimeSlot.MORNING, LocalTime.of(8, 0))), null, Collections.emptyList());

        boolean accepted = invokeVerifyAllConstraints(scheduleBuilder, context, ConstraintEnforcementMode.HARD_ONLY);

        assertTrue(accepted);
        assertEquals(1, scheduleBuilder.getLastConstraintCheckResults().size());
        assertEquals(ConstraintViolationSeverity.SOFT, scheduleBuilder.getLastConstraintCheckResults().get(0).getSeverity());
    }

    @Test
    void softHolidayViolationIsNonBlockingInHardOnlyMode() throws Exception {
        Doctor doctor = buildDoctor();
        Schedule schedule = new Schedule(0L, 10L, new ArrayList<>());
        DoctorUffaPriority dup = new DoctorUffaPriority(doctor, schedule);
        dup.setAssegnazioniTurnoCache(new ArrayList<>());

        Holiday holiday = new Holiday("Natale", HolidayCategory.RELIGIOUS, 7L, 7L, "IT");
        HashMap<Holiday, Boolean> holidayMap = new HashMap<>();
        holidayMap.put(holiday, true);
        DoctorHolidays doctorHolidays = new DoctorHolidays(doctor, holidayMap);

        Constraint softHolidayConstraint = new ConstraintHoliday();
        softHolidayConstraint.setDescription("Festività soft");
        softHolidayConstraint.setViolable(true);

        ScheduleBuilder scheduleBuilder = buildScheduleBuilder(schedule, dup, Collections.singletonList(softHolidayConstraint));
        ContextConstraint context = new ContextConstraint(dup, new ConcreteShift(7L, buildShift(TimeSlot.MORNING, LocalTime.of(8, 0))), doctorHolidays, Collections.singletonList(holiday));

        boolean accepted = invokeVerifyAllConstraints(scheduleBuilder, context, ConstraintEnforcementMode.HARD_ONLY);

        assertTrue(accepted);
        assertEquals(1, scheduleBuilder.getLastConstraintCheckResults().size());
        assertEquals(ConstraintViolationSeverity.SOFT, scheduleBuilder.getLastConstraintCheckResults().get(0).getSeverity());
        assertEquals("Festività soft", scheduleBuilder.getLastConstraintCheckResults().get(0).getDescription());
    }

    private ScheduleBuilder buildScheduleBuilder(Schedule schedule, DoctorUffaPriority dup, List<Constraint> constraints) throws IllegalScheduleException {
        return new ScheduleBuilder(constraints, Collections.singletonList(dup), schedule, Collections.emptyList(), Collections.emptyList());
    }

    private boolean invokeVerifyAllConstraints(ScheduleBuilder builder, ContextConstraint context, ConstraintEnforcementMode mode) throws Exception {
        Method verifyMethod = ScheduleBuilder.class.getDeclaredMethod("verifyAllConstraints", ContextConstraint.class, ConstraintEnforcementMode.class);
        verifyMethod.setAccessible(true);
        return (Boolean) verifyMethod.invoke(builder, context, mode);
    }

    private Doctor buildDoctor() {
        return new Doctor(
                "Mario",
                "Rossi",
                "RSSMRA80A01H501U",
                LocalDate.of(1980, 1, 1),
                "mario.rossi@example.com",
                "password",
                Seniority.STRUCTURED,
                EnumSet.of(SystemActor.DOCTOR)
        );
    }

    private Shift buildShift(TimeSlot slot, LocalTime startTime) {
        Task task = new Task(TaskEnum.WARD);
        MedicalService service = new MedicalService(Collections.singletonList(task), "Cardiology");
        QuantityShiftSeniority qss = new QuantityShiftSeniority(Collections.singletonMap(Seniority.STRUCTURED, 1), task);

        return new Shift(
                startTime,
                Duration.ofHours(8),
                service,
                slot,
                Collections.singletonList(qss),
                EnumSet.allOf(DayOfWeek.class),
                Collections.emptyList()
        );
    }
}
