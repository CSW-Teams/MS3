package org.cswteams.ms3.control.toon;

import lombok.Getter;
import org.cswteams.ms3.entity.ConcreteShift;
import org.cswteams.ms3.entity.Doctor;
import org.cswteams.ms3.entity.DoctorHolidays;
import org.cswteams.ms3.entity.DoctorUffaPriority;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Getter
public class ToonRequestContext {
    private final LocalDate periodStart;
    private final LocalDate periodEnd;
    private final String mode;
    private final List<ConcreteShift> concreteShifts;
    private final List<Doctor> doctors;
    private final List<DoctorUffaPriority> doctorUffaPriorities;
    private final List<DoctorHolidays> doctorHolidays;
    private final List<ToonActiveConstraint> activeConstraints;
    private final List<ToonFeedback> feedbacks;
    private final boolean allowHistoricalFeedbackShiftIds;

    public ToonRequestContext(LocalDate periodStart,
                              LocalDate periodEnd,
                              String mode,
                              List<ConcreteShift> concreteShifts,
                              List<Doctor> doctors,
                              List<DoctorUffaPriority> doctorUffaPriorities,
                              List<DoctorHolidays> doctorHolidays,
                              List<ToonActiveConstraint> activeConstraints,
                              List<ToonFeedback> feedbacks) {
        this(periodStart,
                periodEnd,
                mode,
                concreteShifts,
                doctors,
                doctorUffaPriorities,
                doctorHolidays,
                activeConstraints,
                feedbacks,
                false);
    }

    public ToonRequestContext(LocalDate periodStart,
                              LocalDate periodEnd,
                              String mode,
                              List<ConcreteShift> concreteShifts,
                              List<Doctor> doctors,
                              List<DoctorUffaPriority> doctorUffaPriorities,
                              List<DoctorHolidays> doctorHolidays,
                              List<ToonActiveConstraint> activeConstraints,
                              List<ToonFeedback> feedbacks,
                              boolean allowHistoricalFeedbackShiftIds) {
        this.periodStart = periodStart;
        this.periodEnd = periodEnd;
        this.mode = mode;
        this.concreteShifts = concreteShifts == null ? Collections.emptyList() : new ArrayList<>(concreteShifts);
        this.doctors = doctors == null ? Collections.emptyList() : new ArrayList<>(doctors);
        this.doctorUffaPriorities = doctorUffaPriorities == null ? Collections.emptyList() : new ArrayList<>(doctorUffaPriorities);
        this.doctorHolidays = doctorHolidays == null ? Collections.emptyList() : new ArrayList<>(doctorHolidays);
        this.activeConstraints = activeConstraints == null ? Collections.emptyList() : new ArrayList<>(activeConstraints);
        this.feedbacks = feedbacks == null ? Collections.emptyList() : new ArrayList<>(feedbacks);
        this.allowHistoricalFeedbackShiftIds = allowHistoricalFeedbackShiftIds;
    }
}
