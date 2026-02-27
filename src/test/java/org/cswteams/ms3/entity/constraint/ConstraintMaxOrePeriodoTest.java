package org.cswteams.ms3.entity.constraint;

import org.cswteams.ms3.entity.ConcreteShift;
import org.cswteams.ms3.entity.DoctorUffaPriority;
import org.cswteams.ms3.entity.Schedule;
import org.cswteams.ms3.entity.Shift;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ConstraintMaxOrePeriodoTest {

    @Test
    void verifyConstraintSkipsCachedConcreteShiftWithNullShift() {
        ConstraintMaxOrePeriodo constraint = new ConstraintMaxOrePeriodo(7, 600);
        ContextConstraint context = buildValidContextWithCachedShift(buildCachedShiftWithNullShift());

        assertDoesNotThrow(() -> constraint.verifyConstraint(context));
    }

    @Test
    void verifyConstraintSkipsCachedConcreteShiftWithNullDuration() {
        ConstraintMaxOrePeriodo constraint = new ConstraintMaxOrePeriodo(7, 600);
        ContextConstraint context = buildValidContextWithCachedShift(buildCachedShiftWithNullDuration());

        assertDoesNotThrow(() -> constraint.verifyConstraint(context));
    }

    @Test
    void verifyConstraintThrowsIllegalStateWhenCurrentConcreteShiftDurationIsMissing() {
        ConstraintMaxOrePeriodo constraint = new ConstraintMaxOrePeriodo(7, 600);

        ContextConstraint context = mock(ContextConstraint.class);
        DoctorUffaPriority doctorUffaPriority = mock(DoctorUffaPriority.class);
        Schedule schedule = mock(Schedule.class);
        ConcreteShift currentConcreteShift = mock(ConcreteShift.class);
        Shift currentShift = mock(Shift.class);

        when(context.getDoctorUffaPriority()).thenReturn(doctorUffaPriority);
        when(doctorUffaPriority.getSchedule()).thenReturn(schedule);
        when(schedule.getStartDate()).thenReturn(0L);
        when(schedule.getEndDate()).thenReturn(30L);
        when(doctorUffaPriority.getAssegnazioniTurnoCache()).thenReturn(Collections.emptyList());
        when(context.getConcreteShift()).thenReturn(currentConcreteShift);
        when(currentConcreteShift.getDate()).thenReturn(1L);
        when(currentConcreteShift.getShift()).thenReturn(currentShift);
        when(currentShift.getDuration()).thenReturn(null);

        assertThrows(IllegalStateException.class, () -> constraint.verifyConstraint(context));
    }

    private ContextConstraint buildValidContextWithCachedShift(ConcreteShift malformedCachedShift) {
        ContextConstraint context = mock(ContextConstraint.class);
        DoctorUffaPriority doctorUffaPriority = mock(DoctorUffaPriority.class);
        Schedule schedule = mock(Schedule.class);
        ConcreteShift currentConcreteShift = mock(ConcreteShift.class);
        Shift currentShift = mock(Shift.class);

        when(context.getDoctorUffaPriority()).thenReturn(doctorUffaPriority);
        when(doctorUffaPriority.getSchedule()).thenReturn(schedule);
        when(schedule.getStartDate()).thenReturn(0L);
        when(schedule.getEndDate()).thenReturn(30L);
        when(doctorUffaPriority.getAssegnazioniTurnoCache()).thenReturn(Collections.singletonList(malformedCachedShift));
        when(context.getConcreteShift()).thenReturn(currentConcreteShift);
        when(currentConcreteShift.getDate()).thenReturn(1L);
        when(currentConcreteShift.getShift()).thenReturn(currentShift);
        when(currentShift.getDuration()).thenReturn(Duration.ofHours(8));

        return context;
    }

    private ConcreteShift buildCachedShiftWithNullShift() {
        ConcreteShift concreteShift = mock(ConcreteShift.class);
        when(concreteShift.getDate()).thenReturn(1L);
        when(concreteShift.getShift()).thenReturn(null);
        return concreteShift;
    }

    private ConcreteShift buildCachedShiftWithNullDuration() {
        ConcreteShift concreteShift = mock(ConcreteShift.class);
        Shift shift = mock(Shift.class);
        when(concreteShift.getDate()).thenReturn(1L);
        when(concreteShift.getShift()).thenReturn(shift);
        when(shift.getDuration()).thenReturn(null);
        return concreteShift;
    }
}
