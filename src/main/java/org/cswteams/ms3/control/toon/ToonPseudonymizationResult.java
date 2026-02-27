package org.cswteams.ms3.control.toon;

import org.cswteams.ms3.entity.Doctor;
import org.cswteams.ms3.entity.DoctorHolidays;
import org.cswteams.ms3.entity.DoctorUffaPriority;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class ToonPseudonymizationResult {
    private final List<Doctor> doctors;
    private final List<DoctorUffaPriority> doctorUffaPriorities;
    private final List<DoctorHolidays> doctorHolidays;
    private final List<ToonActiveConstraint> activeConstraints;
    private final List<ToonFeedback> feedbacks;
    private final Map<Long, Long> pseudonymToDoctorId;

    public ToonPseudonymizationResult(List<Doctor> doctors,
                                      List<DoctorUffaPriority> doctorUffaPriorities,
                                      List<DoctorHolidays> doctorHolidays,
                                      List<ToonActiveConstraint> activeConstraints,
                                      List<ToonFeedback> feedbacks,
                                      Map<Long, Long> pseudonymToDoctorId) {
        this.doctors = doctors == null ? Collections.emptyList() : Collections.unmodifiableList(doctors);
        this.doctorUffaPriorities = doctorUffaPriorities == null ? Collections.emptyList() : Collections.unmodifiableList(doctorUffaPriorities);
        this.doctorHolidays = doctorHolidays == null ? Collections.emptyList() : Collections.unmodifiableList(doctorHolidays);
        this.activeConstraints = activeConstraints == null ? Collections.emptyList() : Collections.unmodifiableList(activeConstraints);
        this.feedbacks = feedbacks == null ? Collections.emptyList() : Collections.unmodifiableList(feedbacks);
        this.pseudonymToDoctorId = pseudonymToDoctorId == null ? Collections.emptyMap() : Collections.unmodifiableMap(pseudonymToDoctorId);
    }

    public List<Doctor> getDoctors() {
        return doctors;
    }

    public List<DoctorUffaPriority> getDoctorUffaPriorities() {
        return doctorUffaPriorities;
    }

    public List<DoctorHolidays> getDoctorHolidays() {
        return doctorHolidays;
    }

    public List<ToonActiveConstraint> getActiveConstraints() {
        return activeConstraints;
    }

    public List<ToonFeedback> getFeedbacks() {
        return feedbacks;
    }

    public Map<Long, Long> getPseudonymToDoctorId() {
        return pseudonymToDoctorId;
    }
}
