package org.cswteams.ms3.control.toon;

import org.cswteams.ms3.entity.ConcreteShift;
import org.cswteams.ms3.entity.Doctor;
import org.cswteams.ms3.entity.DoctorHolidays;
import org.cswteams.ms3.entity.DoctorUffaPriority;
import org.cswteams.ms3.entity.Holiday;
import org.cswteams.ms3.entity.Preference;
import org.cswteams.ms3.entity.QuantityShiftSeniority;
import org.cswteams.ms3.entity.Shift;
import org.cswteams.ms3.enums.Seniority;
import org.cswteams.ms3.enums.TimeSlot;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.stream.Collectors;

public class ToonBuilder {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;
    private final ToonValidator validator;

    public ToonBuilder() {
        this.validator = new ToonValidator();
    }

    public String build(ToonRequestContext context) {
        validator.preValidate(context);
        String payload = serialize(context);
        validator.postValidate(payload);
        return payload;
    }

    private String serialize(ToonRequestContext context) {
        StringBuilder builder = new StringBuilder();
        builder.append("# Metadati della sessione\n");
        builder.append("ctx:\n");
        builder.append("period: \"")
                .append(context.getPeriodStart().format(DATE_FORMATTER))
                .append("/")
                .append(context.getPeriodEnd().format(DATE_FORMATTER))
                .append("\"\n");
        builder.append("mode: \"").append(context.getMode()).append("\"\n\n");

        appendShifts(builder, context.getConcreteShifts());
        appendDoctors(builder, context.getDoctors(), context.getDoctorUffaPriorities(), context.getDoctorHolidays());
        appendFeedbacks(builder, context.getFeedbacks());
        appendConstraints(builder, context.getActiveConstraints());
        return builder.toString();
    }

    private void appendShifts(StringBuilder builder, List<ConcreteShift> concreteShifts) {
        builder.append("# Catalogo Turni (ConcreteShifts da coprire)\n");
        builder.append("# Formato Tabellare per efficienza token\n");
        List<ConcreteShift> ordered = new ArrayList<>(concreteShifts);
        ordered.sort(Comparator
                .comparingLong(ConcreteShift::getDate)
                .thenComparing(shift -> shift.getShift().getTimeSlot().name())
                .thenComparing(shift -> shift.getShift().getId() == null ? 0L : shift.getShift().getId()));
        builder.append("shifts[").append(ordered.size())
                .append("]{id, slot, date, duration, req_str, req_jun}:\n");
        for (ConcreteShift shift : ordered) {
            Shift abstractShift = shift.getShift();
            Map<String, Integer> requirements = computeShiftRequirements(abstractShift);
            builder.append(shiftIdFor(shift)).append(", ")
                    .append(abstractShift.getTimeSlot().name()).append(", ")
                    .append(ToonValidator.toLocalDate(shift.getDate()).format(DATE_FORMATTER)).append(", ")
                    .append(abstractShift.getDuration().toMinutes()).append(", ")
                    .append(requirements.get("req_str")).append(", ")
                    .append(requirements.get("req_jun")).append("\n");
        }
        builder.append("\n");
    }

    private Map<String, Integer> computeShiftRequirements(Shift shift) {
        int reqStructured = 0;
        int reqJunior = 0;
        if (shift.getQuantityShiftSeniority() != null) {
            for (QuantityShiftSeniority quantity : shift.getQuantityShiftSeniority()) {
                if (quantity.getSeniorityMap() == null) {
                    continue;
                }
                for (Map.Entry<Seniority, Integer> entry : quantity.getSeniorityMap().entrySet()) {
                    if (entry.getKey() == Seniority.STRUCTURED) {
                        reqStructured += entry.getValue();
                    } else {
                        reqJunior += entry.getValue();
                    }
                }
            }
        }
        Map<String, Integer> result = new HashMap<>();
        result.put("req_str", reqStructured);
        result.put("req_jun", reqJunior);
        return result;
    }

    private void appendDoctors(StringBuilder builder,
                               List<Doctor> doctors,
                               List<DoctorUffaPriority> priorities,
                               List<DoctorHolidays> doctorHolidays) {
        builder.append("# Registro Medici\n");
        List<Doctor> ordered = new ArrayList<>(doctors);
        ordered.sort(Comparator.comparingLong(Doctor::getId));
        Map<Long, DoctorUffaPriority> priorityMap = priorities.stream()
                .collect(Collectors.toMap(p -> p.getDoctor().getId(), p -> p, (left, right) -> left));
        Map<Long, DoctorHolidays> holidaysMap = doctorHolidays.stream()
                .collect(Collectors.toMap(h -> h.getDoctor().getId(), h -> h, (left, right) -> left));
        builder.append("doctors[").append(ordered.size()).append("]:\n");
        for (Doctor doctor : ordered) {
            builder.append("- id: ").append(doctor.getId()).append("\n");
            builder.append("  role: ").append(mapSeniority(doctor.getSeniority())).append("\n");
            DoctorUffaPriority priority = priorityMap.get(doctor.getId());
            if (priority == null) {
                priority = new DoctorUffaPriority(doctor);
            }
            builder.append("  priorities{gen, night, long}: ")
                    .append(priority.getGeneralPriority()).append(", ")
                    .append(priority.getNightPriority()).append(", ")
                    .append(priority.getLongShiftPriority()).append("\n");
            List<String> holidayTokens = resolveHolidayTokens(holidaysMap.get(doctor.getId()));
            builder.append("  holidays_taken[").append(holidayTokens.size()).append("]: ");
            if (holidayTokens.isEmpty()) {
                builder.append("[]\n");
            } else {
                builder.append(holidayTokens.stream()
                        .map(token -> "\"" + token + "\"")
                        .collect(Collectors.joining(", ")))
                        .append("\n");
            }
            appendBlocks(builder, doctor);
        }
        builder.append("\n");
    }

    private void appendBlocks(StringBuilder builder, Doctor doctor) {
        List<Preference> preferences = doctor.getPreferenceList();
        List<Preference> ordered = new ArrayList<>(preferences);
        ordered.sort(Comparator.comparing(Preference::getDate));
        builder.append("  blocks[").append(ordered.size()).append("]:\n");
        for (Preference preference : ordered) {
            LocalDate date = preference.getDate();
            Set<TimeSlot> slots = new TreeSet<>(Comparator.comparing(Enum::name));
            if (preference.getTimeSlots() != null) {
                slots.addAll(preference.getTimeSlots());
            }
            builder.append("  - start: ").append(date.format(DATE_FORMATTER))
                    .append(", end: ").append(date.format(DATE_FORMATTER))
                    .append(", slots: [");
            builder.append(slots.stream()
                    .map(slot -> "\"" + slot.name() + "\"")
                    .collect(Collectors.joining(", ")));
            builder.append("]\n");
        }
    }

    private void appendFeedbacks(StringBuilder builder, List<ToonFeedback> feedbacks) {
        if (feedbacks.isEmpty()) {
            return;
        }
        List<ToonFeedback> ordered = new ArrayList<>(feedbacks);
        ordered.sort(Comparator
                .comparing(ToonFeedback::getShiftId)
                .thenComparing(ToonFeedback::getDoctorId));
        builder.append("# Feedbacks\n");
        builder.append("feedbacks[").append(ordered.size())
                .append("]{shift_id, doctor_id, reason_code, severity, comment}:\n");
        for (ToonFeedback feedback : ordered) {
            builder.append(feedback.getShiftId()).append(", ")
                    .append(feedback.getDoctorId()).append(", ")
                    .append(feedback.getReasonCode()).append(", ")
                    .append(feedback.getSeverity()).append(", ")
                    .append(renderFeedbackComment(feedback.getComment()))
                    .append("\n");
        }
        builder.append("\n");
    }

    private void appendConstraints(StringBuilder builder, List<ToonActiveConstraint> constraints) {
        builder.append("# Vincoli di Business\n");
        List<ToonActiveConstraint> ordered = new ArrayList<>(constraints);
        ordered.sort(Comparator
                .comparing(ToonActiveConstraint::getType)
                .thenComparing(ToonActiveConstraint::getEntityType)
                .thenComparing(ToonActiveConstraint::getEntityId));
        builder.append("active_constraints[").append(ordered.size())
                .append("]{type, entity_type, entity_id, reason, params}:\n");
        for (ToonActiveConstraint constraint : ordered) {
            builder.append(constraint.getType().name()).append(", ")
                    .append(constraint.getEntityType().name()).append(", ")
                    .append(constraint.getEntityId()).append(", ")
                    .append(constraint.getReason()).append(", ")
                    .append(renderParams(constraint.getParams()))
                    .append("\n");
        }
    }

    private String renderParams(Map<String, String> params) {
        if (params.isEmpty()) {
            return "{}";
        }
        Map<String, String> ordered = new TreeMap<>(params);
        return ordered.entrySet().stream()
                .map(entry -> "\"" + entry.getKey() + "\": \"" + entry.getValue() + "\"")
                .collect(Collectors.joining(", ", "{ ", " }"));
    }

    private String renderFeedbackComment(String comment) {
        if (comment == null) {
            return "\"\"";
        }
        String escaped = comment
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r");
        return "\"" + escaped + "\"";
    }

    private List<String> resolveHolidayTokens(DoctorHolidays doctorHolidays) {
        if (doctorHolidays == null || doctorHolidays.getHolidayMap() == null) {
            return List.of();
        }
        Set<String> tokens = new TreeSet<>();
        int fallbackIndex = 1;
        for (Holiday holiday : doctorHolidays.getHolidayMap().keySet()) {
            if (holiday == null) {
                continue;
            }
            if (holiday.getId() != null) {
                tokens.add("HOLIDAY_" + holiday.getId());
            } else {
                tokens.add("HOLIDAY_" + fallbackIndex);
                fallbackIndex++;
            }
        }
        return new ArrayList<>(tokens);
    }

    private String mapSeniority(Seniority seniority) {
        if (seniority == Seniority.STRUCTURED) {
            return "STRUCTURED";
        }
        return "JUNIOR";
    }

    public static String shiftIdFor(ConcreteShift shift) {
        LocalDate date = ToonValidator.toLocalDate(shift.getDate());
        Long shiftId = shift.getShift() == null ? null : shift.getShift().getId();
        String baseId = shiftId == null ? "X" : String.valueOf(shiftId);
        return "S_" + baseId + "_" + date.format(DateTimeFormatter.BASIC_ISO_DATE);
    }
}
