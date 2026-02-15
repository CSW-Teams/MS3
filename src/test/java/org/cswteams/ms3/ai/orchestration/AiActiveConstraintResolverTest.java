package org.cswteams.ms3.ai.orchestration;

import org.cswteams.ms3.control.toon.ToonActiveConstraint;
import org.cswteams.ms3.control.toon.ToonBuilder;
import org.cswteams.ms3.control.toon.ToonConstraintEntityType;
import org.cswteams.ms3.control.toon.ToonConstraintType;
import org.cswteams.ms3.dao.ConstraintDAO;
import org.cswteams.ms3.entity.ConcreteShift;
import org.cswteams.ms3.entity.Doctor;
import org.cswteams.ms3.entity.MedicalService;
import org.cswteams.ms3.entity.QuantityShiftSeniority;
import org.cswteams.ms3.entity.Shift;
import org.cswteams.ms3.entity.Task;
import org.cswteams.ms3.entity.constraint.AdditionalConstraint;
import org.cswteams.ms3.entity.constraint.Constraint;
import org.cswteams.ms3.entity.constraint.ConstraintHoliday;
import org.cswteams.ms3.entity.constraint.ConstraintMaxOrePeriodo;
import org.cswteams.ms3.entity.constraint.ConstraintNumeroDiRuoloTurno;
import org.cswteams.ms3.enums.Seniority;
import org.cswteams.ms3.enums.SystemActor;
import org.cswteams.ms3.enums.TaskEnum;
import org.cswteams.ms3.enums.TimeSlot;
import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AiActiveConstraintResolverTest {

    @Test
    void resolveMapsViolableFlagToSoftAndHardTypes() {
        ConstraintDAO constraintDAO = mock(ConstraintDAO.class);

        ConstraintHoliday softConstraint = new ConstraintHoliday();
        softConstraint.setId(31L);
        softConstraint.setDescription("Soft holiday preference");
        softConstraint.setViolable(true);

        ConstraintHoliday hardConstraint = new ConstraintHoliday();
        hardConstraint.setId(32L);
        hardConstraint.setDescription("Hard holiday rule");
        hardConstraint.setViolable(false);

        when(constraintDAO.findAll()).thenReturn(List.of(softConstraint, hardConstraint));

        AiActiveConstraintResolver resolver = new AiActiveConstraintResolver(constraintDAO);

        List<ToonActiveConstraint> mapped = resolver.resolve(
                List.of(newDoctor(9L)),
                List.of(newShift(200L, LocalDate.of(2026, 2, 1)))
        );

        assertEquals(2, mapped.size());
        assertEquals(ToonConstraintType.SOFT, mapped.get(0).getType());
        assertEquals(ToonConstraintType.HARD, mapped.get(1).getType());
    }

    @Test
    void resolveMapsSupportedConstraintsWithDeterministicFields() {
        ConstraintDAO constraintDAO = mock(ConstraintDAO.class);
        ConstraintMaxOrePeriodo hardConstraint = new ConstraintMaxOrePeriodo(7, 480);
        hardConstraint.setId(10L);
        hardConstraint.setDescription("Max hours by period");
        hardConstraint.setViolable(false);

        ConstraintNumeroDiRuoloTurno softConstraint = new ConstraintNumeroDiRuoloTurno();
        softConstraint.setId(20L);
        softConstraint.setDescription("Role quota per shift");
        softConstraint.setViolable(true);

        AdditionalConstraint unsupported = new AdditionalConstraint();
        unsupported.setId(30L);
        unsupported.setDescription("Unsupported");
        unsupported.setViolable(false);

        when(constraintDAO.findAll()).thenReturn(List.of(hardConstraint, softConstraint, unsupported));

        AiActiveConstraintResolver resolver = new AiActiveConstraintResolver(constraintDAO);

        List<Doctor> doctors = List.of(newDoctor(7L), newDoctor(2L));
        List<ConcreteShift> shifts = List.of(newShift(100L, LocalDate.of(2026, 1, 10)));

        List<ToonActiveConstraint> mapped = resolver.resolve(doctors, shifts);

        assertEquals(2, mapped.size());

        ToonActiveConstraint first = mapped.get(0);
        assertEquals(ToonConstraintType.HARD, first.getType());
        assertEquals(ToonConstraintEntityType.DOCTOR, first.getEntityType());
        assertEquals("2", first.getEntityId());
        assertEquals("Max hours by period", first.getReason());
        assertEquals("7", first.getParams().get("period_duration_days"));
        assertEquals("480", first.getParams().get("period_max_time_minutes"));

        ToonActiveConstraint second = mapped.get(1);
        assertEquals(ToonConstraintType.SOFT, second.getType());
        assertEquals(ToonConstraintEntityType.SHIFT, second.getEntityType());
        assertEquals(ToonBuilder.shiftIdFor(shifts.get(0)), second.getEntityId());
        assertEquals("Role quota per shift", second.getReason());
        assertEquals("ConstraintNumeroDiRuoloTurno", second.getParams().get("constraint_class"));
    }

    @Test
    void resolveSkipsConstraintWhenEntityReferenceIsMissing() {
        ConstraintDAO constraintDAO = mock(ConstraintDAO.class);
        Constraint constraint = new ConstraintMaxOrePeriodo(7, 480);
        constraint.setId(11L);
        constraint.setDescription("Max hours");
        constraint.setViolable(false);

        when(constraintDAO.findAll()).thenReturn(List.of(constraint));

        AiActiveConstraintResolver resolver = new AiActiveConstraintResolver(constraintDAO);
        List<ToonActiveConstraint> mapped = resolver.resolve(List.of(), List.of());

        assertTrue(mapped.isEmpty());
    }

    @Test
    void resolveWithReportTracksResolvedSkippedAndHardSoftSplit() {
        ConstraintDAO constraintDAO = mock(ConstraintDAO.class);

        ConstraintHoliday hardConstraint = new ConstraintHoliday();
        hardConstraint.setId(41L);
        hardConstraint.setDescription("Hard holiday rule");
        hardConstraint.setViolable(false);

        ConstraintNumeroDiRuoloTurno softConstraint = new ConstraintNumeroDiRuoloTurno();
        softConstraint.setId(42L);
        softConstraint.setDescription("Soft role quota");
        softConstraint.setViolable(true);

        ConstraintMaxOrePeriodo skippedConstraint = new ConstraintMaxOrePeriodo(7, 480);
        skippedConstraint.setId(43L);
        skippedConstraint.setDescription("Will be skipped because no doctor");
        skippedConstraint.setViolable(false);

        when(constraintDAO.findAll()).thenReturn(List.of(hardConstraint, softConstraint, skippedConstraint));

        AiActiveConstraintResolver resolver = new AiActiveConstraintResolver(constraintDAO);

        AiActiveConstraintResolver.ResolveResult result = resolver.resolveWithReport(
                List.of(),
                List.of(newShift(200L, LocalDate.of(2026, 2, 1))),
                false
        );

        assertEquals(1, result.getResolvedConstraints().size());
        assertEquals(2, result.getSkippedConstraints());
        assertEquals(0, result.getHardConstraintsCount());
        assertEquals(1, result.getSoftConstraintsCount());
    }

    @Test
    void resolveWithReportFailsFastWhenPolicyRequiresIt() {
        ConstraintDAO constraintDAO = mock(ConstraintDAO.class);

        ConstraintMaxOrePeriodo skippedConstraint = new ConstraintMaxOrePeriodo(7, 480);
        skippedConstraint.setId(51L);
        skippedConstraint.setDescription("No doctors available");
        skippedConstraint.setViolable(false);

        when(constraintDAO.findAll()).thenReturn(List.of(skippedConstraint));

        AiActiveConstraintResolver resolver = new AiActiveConstraintResolver(constraintDAO);

        assertThrows(IllegalStateException.class,
                () -> resolver.resolveWithReport(List.of(), List.of(), true));
    }

    private Doctor newDoctor(Long id) {
        Doctor doctor = new Doctor(
                "Mario",
                "Rossi",
                "TAXCODEX00000000",
                LocalDate.of(1980, 1, 1),
                "doctor@example.com",
                "secret",
                Seniority.STRUCTURED,
                Set.of(SystemActor.DOCTOR)
        );
        doctor.setId(id);
        return doctor;
    }

    private ConcreteShift newShift(Long shiftId, LocalDate date) {
        Task task = new Task(TaskEnum.CLINIC);
        MedicalService service = new MedicalService(List.of(task), "Ward");
        QuantityShiftSeniority quantity = new QuantityShiftSeniority(Map.of(Seniority.STRUCTURED, 1), task);
        Shift shift = new Shift(
                shiftId,
                TimeSlot.MORNING,
                LocalTime.of(8, 0),
                Duration.ofMinutes(360),
                Set.of(DayOfWeek.MONDAY),
                service,
                List.of(quantity),
                List.of()
        );
        return new ConcreteShift(date.toEpochDay(), shift);
    }
}
