package org.cswteams.ms3.control.toon;

import org.cswteams.ms3.entity.ConcreteShift;
import org.cswteams.ms3.entity.Doctor;
import org.cswteams.ms3.entity.DoctorHolidays;
import org.cswteams.ms3.entity.DoctorUffaPriority;
import org.cswteams.ms3.entity.Shift;
import org.cswteams.ms3.enums.Seniority;
import org.cswteams.ms3.enums.TimeSlot;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ToonValidator {
    private static final List<String> REQUIRED_LEGACY_SECTION_MARKERS = List.of(
            "ctx:",
            "period:",
            "mode:",
            "shifts[",
            "doctors["
    );
    private static final List<String> REQUIRED_COMPACT_SECTION_MARKERS = List.of(
            "ctx:{p:\"",
            ",m:\"",
            "sh[",
            "dr["
    );
    private static final List<String> PII_MARKERS = List.of(
            "name:",
            "lastname:",
            "email",
            "tax_code",
            "password",
            "birthday",
            "@"
    );

    public void preValidate(ToonRequestContext context) {
        if (context == null) {
            throw new ToonValidationException("TOON context is required");
        }
        if (context.getPeriodStart() == null || context.getPeriodEnd() == null) {
            throw new ToonValidationException("Period start/end are required");
        }
        if (context.getPeriodEnd().isBefore(context.getPeriodStart())) {
            throw new ToonValidationException("Period end must be after start");
        }
        if (context.getMode() == null || context.getMode().trim().isEmpty()) {
            throw new ToonValidationException("Mode is required");
        }
        if (context.getConcreteShifts().isEmpty()) {
            throw new ToonValidationException("At least one concrete shift is required");
        }
        if (context.getDoctors().isEmpty()) {
            throw new ToonValidationException("At least one doctor is required");
        }
        validateDoctors(context.getDoctors());
        validateShifts(context.getConcreteShifts());
        validatePriorities(context.getDoctorUffaPriorities(), context.getDoctors());
        validateDoctorHolidays(context.getDoctorHolidays(), context.getDoctors());
        validateConstraints(context.getActiveConstraints(), context.getConcreteShifts(), context.getDoctors());
        validateFeedbacks(context.getFeedbacks(), context.getConcreteShifts(), context.getDoctors());
    }

    public void postValidate(String toonPayload, ToonBuilder.SerializationMode mode) {
        if (toonPayload == null || toonPayload.trim().isEmpty()) {
            throw new ToonValidationException("Generated TOON payload is empty");
        }
        ToonBuilder.SerializationMode selectedMode = mode == null
                ? ToonBuilder.SerializationMode.LEGACY
                : mode;
        List<String> requiredMarkers = selectedMode == ToonBuilder.SerializationMode.COMPACT
                ? REQUIRED_COMPACT_SECTION_MARKERS
                : REQUIRED_LEGACY_SECTION_MARKERS;
        for (String marker : requiredMarkers) {
            if (!toonPayload.contains(marker)) {
                throw new ToonValidationException("Missing required TOON section: " + marker);
            }
        }
        validateOptionalSectionCount(toonPayload,
                selectedMode == ToonBuilder.SerializationMode.COMPACT ? "fb[" : "feedbacks[");
        validateOptionalSectionCount(toonPayload,
                selectedMode == ToonBuilder.SerializationMode.COMPACT ? "ac[" : "active_constraints[");
        for (String marker : PII_MARKERS) {
            if (toonPayload.contains(marker)) {
                throw new ToonValidationException("PII marker found in TOON payload: " + marker);
            }
        }
    }

    private void validateOptionalSectionCount(String toonPayload, String sectionMarker) {
        int markerIndex = toonPayload.indexOf(sectionMarker);
        if (markerIndex < 0) {
            return;
        }
        int countStart = markerIndex + sectionMarker.length();
        int countEnd = toonPayload.indexOf("]", countStart);
        if (countEnd < 0) {
            throw new ToonValidationException("Malformed TOON section header: " + sectionMarker);
        }
        String rawCount = toonPayload.substring(countStart, countEnd).trim();
        if (rawCount.isEmpty()) {
            throw new ToonValidationException("Missing TOON section count: " + sectionMarker);
        }
        try {
            int count = Integer.parseInt(rawCount);
            if (count < 0) {
                throw new ToonValidationException("Negative TOON section count: " + sectionMarker);
            }
        } catch (NumberFormatException ex) {
            throw new ToonValidationException("Invalid TOON section count: " + sectionMarker);
        }
    }

    private void validateDoctors(List<Doctor> doctors) {
        for (Doctor doctor : doctors) {
            if (doctor == null || doctor.getId() == null) {
                throw new ToonValidationException("Doctor and doctor id are required");
            }
            Seniority seniority = doctor.getSeniority();
            if (seniority == null) {
                throw new ToonValidationException("Doctor seniority is required for id " + doctor.getId());
            }
        }
    }

    private void validateShifts(List<ConcreteShift> shifts) {
        for (ConcreteShift shift : shifts) {
            if (shift == null) {
                throw new ToonValidationException("Concrete shift entry is null");
            }
            if (shift.getShift() == null) {
                throw new ToonValidationException("Concrete shift requires shift definition");
            }
            Shift abstractShift = shift.getShift();
            TimeSlot timeSlot = abstractShift.getTimeSlot();
            if (timeSlot == null) {
                throw new ToonValidationException("Shift timeslot is required");
            }
            if (abstractShift.getDuration() == null) {
                throw new ToonValidationException("Shift duration is required");
            }
        }
    }

    private void validatePriorities(List<DoctorUffaPriority> priorities, List<Doctor> doctors) {
        Set<Long> doctorIds = new HashSet<>();
        for (Doctor doctor : doctors) {
            doctorIds.add(doctor.getId());
        }
        for (DoctorUffaPriority priority : priorities) {
            if (priority == null || priority.getDoctor() == null) {
                throw new ToonValidationException("DoctorUffaPriority requires doctor reference");
            }
            if (!doctorIds.contains(priority.getDoctor().getId())) {
                throw new ToonValidationException("Priority doctor not in doctor list: " + priority.getDoctor().getId());
            }
        }
    }

    private void validateDoctorHolidays(List<DoctorHolidays> doctorHolidays, List<Doctor> doctors) {
        Set<Long> doctorIds = new HashSet<>();
        for (Doctor doctor : doctors) {
            doctorIds.add(doctor.getId());
        }
        for (DoctorHolidays holidays : doctorHolidays) {
            if (holidays == null || holidays.getDoctor() == null) {
                throw new ToonValidationException("DoctorHolidays requires doctor reference");
            }
            if (!doctorIds.contains(holidays.getDoctor().getId())) {
                throw new ToonValidationException("DoctorHolidays doctor not in doctor list: " + holidays.getDoctor().getId());
            }
        }
    }

    private void validateConstraints(List<ToonActiveConstraint> constraints,
                                     List<ConcreteShift> shifts,
                                     List<Doctor> doctors) {
        Set<String> shiftIds = new HashSet<>();
        for (ConcreteShift shift : shifts) {
            shiftIds.add(ToonBuilder.shiftIdFor(shift));
        }
        Set<String> doctorIds = new HashSet<>();
        for (Doctor doctor : doctors) {
            doctorIds.add(String.valueOf(doctor.getId()));
        }
        for (ToonActiveConstraint constraint : constraints) {
            if (constraint.getType() == null || constraint.getEntityType() == null) {
                throw new ToonValidationException("Constraint requires type and entity type");
            }
            if (constraint.getEntityId() == null || constraint.getEntityId().trim().isEmpty()) {
                throw new ToonValidationException("Constraint entity id is required");
            }
            if (constraint.getEntityType() == ToonConstraintEntityType.SHIFT && !shiftIds.contains(constraint.getEntityId())) {
                throw new ToonValidationException("Constraint shift id not found: " + constraint.getEntityId());
            }
            if (constraint.getEntityType() == ToonConstraintEntityType.DOCTOR && !doctorIds.contains(constraint.getEntityId())) {
                throw new ToonValidationException("Constraint doctor id not found: " + constraint.getEntityId());
            }
            if (constraint.getReason() == null || constraint.getReason().trim().isEmpty()) {
                throw new ToonValidationException("Constraint reason is required");
            }
            for (Map.Entry<String, String> entry : constraint.getParams().entrySet()) {
                if (entry.getKey() == null || entry.getKey().trim().isEmpty()) {
                    throw new ToonValidationException("Constraint param key is required");
                }
                if (entry.getValue() == null || entry.getValue().trim().isEmpty()) {
                    throw new ToonValidationException("Constraint param value is required");
                }
            }
        }
    }

    private void validateFeedbacks(List<ToonFeedback> feedbacks,
                                   List<ConcreteShift> shifts,
                                   List<Doctor> doctors) {
        Set<String> shiftIds = new HashSet<>();
        for (ConcreteShift shift : shifts) {
            shiftIds.add(ToonBuilder.shiftIdFor(shift));
        }
        Set<Long> doctorIds = new HashSet<>();
        for (Doctor doctor : doctors) {
            doctorIds.add(doctor.getId());
        }
        for (ToonFeedback feedback : feedbacks) {
            if (feedback.getShiftId() == null || feedback.getShiftId().trim().isEmpty()) {
                throw new ToonValidationException("Feedback shift id is required");
            }
            if (!shiftIds.contains(feedback.getShiftId())) {
                throw new ToonValidationException("Feedback shift id not found: " + feedback.getShiftId());
            }
            if (feedback.getDoctorId() == null || !doctorIds.contains(feedback.getDoctorId())) {
                throw new ToonValidationException("Feedback doctor id not found: " + feedback.getDoctorId());
            }
            if (feedback.getReasonCode() == null || feedback.getReasonCode().trim().isEmpty()) {
                throw new ToonValidationException("Feedback reason code is required");
            }
            if (!ToonFeedback.Category.isKnown(feedback.getReasonCode())) {
                throw new ToonValidationException("Feedback reason code is invalid: " + feedback.getReasonCode());
            }
            if (feedback.getSeverity() < 1 || feedback.getSeverity() > 6) {
                throw new ToonValidationException("Feedback severity must be 1-6");
            }
        }
    }

    public static LocalDate toLocalDate(long epochDay) {
        return LocalDate.ofEpochDay(epochDay);
    }
}
