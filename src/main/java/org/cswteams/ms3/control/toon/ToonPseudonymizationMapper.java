package org.cswteams.ms3.control.toon;

import org.cswteams.ms3.entity.Doctor;
import org.cswteams.ms3.entity.DoctorHolidays;
import org.cswteams.ms3.entity.DoctorUffaPriority;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ToonPseudonymizationMapper {
    public ToonPseudonymizationResult pseudonymize(List<Doctor> doctors,
                                                   List<DoctorUffaPriority> priorities,
                                                   List<DoctorHolidays> doctorHolidays,
                                                   List<ToonActiveConstraint> activeConstraints,
                                                   List<ToonFeedback> feedbacks) {
        List<Doctor> safeDoctors = doctors == null ? List.of() : doctors;
        Map<Long, Long> doctorIdToPseudonym = new LinkedHashMap<>();
        Map<Long, Long> pseudonymToDoctorId = new LinkedHashMap<>();
        Map<Long, Doctor> pseudonymizedDoctorsByOriginalId = new HashMap<>();
        List<Doctor> pseudonymizedDoctors = new ArrayList<>();

        List<Doctor> ordered = new ArrayList<>(safeDoctors);
        ordered.sort(Comparator.comparing(Doctor::getId, Comparator.nullsLast(Long::compareTo)));
        long nextPseudonym = 1L;
        for (Doctor doctor : ordered) {
            Long originalId = doctor.getId();
            if (originalId == null) {
                throw new IllegalArgumentException("Doctor id is required for pseudonymization");
            }
            long pseudonymId = nextPseudonym++;
            doctorIdToPseudonym.put(originalId, pseudonymId);
            pseudonymToDoctorId.put(pseudonymId, originalId);
            Doctor pseudonymizedDoctor = cloneDoctorWithId(doctor, pseudonymId);
            pseudonymizedDoctorsByOriginalId.put(originalId, pseudonymizedDoctor);
            pseudonymizedDoctors.add(pseudonymizedDoctor);
        }

        List<DoctorUffaPriority> pseudonymizedPriorities = new ArrayList<>();
        if (priorities != null) {
            for (DoctorUffaPriority priority : priorities) {
                if (priority == null || priority.getDoctor() == null) {
                    continue;
                }
                Doctor pseudonymizedDoctor = resolveDoctor(pseudonymizedDoctorsByOriginalId, priority.getDoctor());
                DoctorUffaPriority pseudonymizedPriority = new DoctorUffaPriority(pseudonymizedDoctor);
                pseudonymizedPriority.setGeneralPriority(priority.getGeneralPriority());
                pseudonymizedPriority.setNightPriority(priority.getNightPriority());
                pseudonymizedPriority.setLongShiftPriority(priority.getLongShiftPriority());
                pseudonymizedPriority.setPartialGeneralPriority(priority.getPartialGeneralPriority());
                pseudonymizedPriority.setPartialNightPriority(priority.getPartialNightPriority());
                pseudonymizedPriority.setPartialLongShiftPriority(priority.getPartialLongShiftPriority());
                pseudonymizedPriorities.add(pseudonymizedPriority);
            }
        }

        List<DoctorHolidays> pseudonymizedHolidays = new ArrayList<>();
        if (doctorHolidays != null) {
            for (DoctorHolidays holidays : doctorHolidays) {
                if (holidays == null || holidays.getDoctor() == null) {
                    continue;
                }
                Doctor pseudonymizedDoctor = resolveDoctor(pseudonymizedDoctorsByOriginalId, holidays.getDoctor());
                DoctorHolidays pseudonymized = new DoctorHolidays(
                        pseudonymizedDoctor,
                        holidays.getHolidayMap() == null ? new HashMap<>() : new HashMap<>(holidays.getHolidayMap())
                );
                pseudonymizedHolidays.add(pseudonymized);
            }
        }

        List<ToonFeedback> pseudonymizedFeedbacks = new ArrayList<>();
        if (feedbacks != null) {
            for (ToonFeedback feedback : feedbacks) {
                if (feedback == null) {
                    continue;
                }
                Long pseudonymId = doctorIdToPseudonym.get(feedback.getDoctorId());
                if (pseudonymId == null) {
                    throw new IllegalArgumentException("Feedback doctor id not in scope: " + feedback.getDoctorId());
                }
                pseudonymizedFeedbacks.add(new ToonFeedback(
                        feedback.getShiftId(),
                        pseudonymId,
                        feedback.getReasonCode(),
                        feedback.getSeverity(),
                        feedback.getComment()
                ));
            }
        }

        List<ToonActiveConstraint> pseudonymizedConstraints = new ArrayList<>();
        if (activeConstraints != null) {
            for (ToonActiveConstraint constraint : activeConstraints) {
                if (constraint == null) {
                    continue;
                }
                if (constraint.getEntityType() == ToonConstraintEntityType.DOCTOR) {
                    Long originalId = parseDoctorId(constraint.getEntityId());
                    Long pseudonymId = doctorIdToPseudonym.get(originalId);
                    if (pseudonymId == null) {
                        throw new IllegalArgumentException("Constraint doctor id not in scope: " + constraint.getEntityId());
                    }
                    pseudonymizedConstraints.add(new ToonActiveConstraint(
                            constraint.getType(),
                            constraint.getEntityType(),
                            String.valueOf(pseudonymId),
                            constraint.getReason(),
                            constraint.getParams()
                    ));
                } else {
                    pseudonymizedConstraints.add(constraint);
                }
            }
        }

        return new ToonPseudonymizationResult(
                pseudonymizedDoctors,
                pseudonymizedPriorities,
                pseudonymizedHolidays,
                pseudonymizedConstraints,
                pseudonymizedFeedbacks,
                pseudonymToDoctorId
        );
    }

    private Doctor resolveDoctor(Map<Long, Doctor> pseudonymizedDoctorsByOriginalId, Doctor original) {
        Long originalId = original.getId();
        if (originalId == null) {
            throw new IllegalArgumentException("Doctor id is required for pseudonymization");
        }
        Doctor pseudonymizedDoctor = pseudonymizedDoctorsByOriginalId.get(originalId);
        if (pseudonymizedDoctor == null) {
            throw new IllegalArgumentException("Doctor id not in scope: " + originalId);
        }
        return pseudonymizedDoctor;
    }

    private Long parseDoctorId(String entityId) {
        if (entityId == null) {
            throw new IllegalArgumentException("Constraint doctor id is required");
        }
        try {
            return Long.valueOf(entityId);
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("Constraint doctor id must be numeric: " + entityId, ex);
        }
    }

    private Doctor cloneDoctorWithId(Doctor doctor, Long pseudonymId) {
        Objects.requireNonNull(doctor, "Doctor is required");
        Doctor clone = new Doctor(
                doctor.getName(),
                doctor.getLastname(),
                doctor.getTaxCode(),
                doctor.getBirthday(),
                doctor.getEmail(),
                doctor.getPassword(),
                doctor.getSeniority(),
                doctor.getSystemActors()
        );
        if (doctor.getPreferenceList() != null) {
            clone.getPreferenceList().addAll(doctor.getPreferenceList());
        }
        if (doctor.getSpecializations() != null) {
            clone.getSpecializations().addAll(doctor.getSpecializations());
        }
        setDoctorId(clone, pseudonymId);
        return clone;
    }

    private void setDoctorId(Doctor doctor, Long pseudonymId) {
        try {
            Field field = Doctor.class.getDeclaredField("id");
            field.setAccessible(true);
            field.set(doctor, pseudonymId);
        } catch (Exception ex) {
            throw new IllegalStateException("Unable to set pseudonymized doctor id", ex);
        }
    }
}
