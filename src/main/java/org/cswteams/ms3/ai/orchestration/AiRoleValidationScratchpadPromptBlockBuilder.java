package org.cswteams.ms3.ai.orchestration;

import org.cswteams.ms3.control.toon.ToonBuilder;
import org.cswteams.ms3.entity.ConcreteShift;
import org.cswteams.ms3.entity.Doctor;
import org.cswteams.ms3.entity.QuantityShiftSeniority;
import org.cswteams.ms3.entity.Shift;
import org.cswteams.ms3.enums.Seniority;
import org.cswteams.ms3.enums.TimeSlot;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class AiRoleValidationScratchpadPromptBlockBuilder {

    private static final List<Seniority> ROLE_ORDER = List.of(
            Seniority.STRUCTURED,
            Seniority.SPECIALIST_JUNIOR,
            Seniority.SPECIALIST_SENIOR
    );

    public String buildRoleValidationScratchpadBlock(List<ConcreteShift> concreteShifts, List<Doctor> doctors) {
        List<ConcreteShift> orderedShifts = new ArrayList<>(concreteShifts == null ? List.of() : concreteShifts);
        orderedShifts.sort(Comparator
                .comparingLong(ConcreteShift::getDate)
                .thenComparing(shift -> {
                    Shift concreteShift = shift.getShift();
                    return concreteShift == null || concreteShift.getTimeSlot() == null
                            ? TimeSlot.MORNING
                            : concreteShift.getTimeSlot();
                })
                .thenComparing(shift -> shift.getShift().getId() == null ? 0L : shift.getShift().getId()));

        Map<Seniority, List<Long>> candidatesByRole = mapCandidateDoctorIdsByRole(doctors);
        List<String> rows = new ArrayList<>();
        List<Long> doctorIds;
        for (ConcreteShift concreteShift : orderedShifts) {
            Map<Seniority, Integer> requiredByRole = calculateRequiredByRole(concreteShift == null ? null : concreteShift.getShift());
            for (Seniority role : ROLE_ORDER) {
                doctorIds = candidatesByRole.getOrDefault(role, List.of());
                Collections.shuffle(doctorIds);
                int required = requiredByRole.getOrDefault(role, 0);
                if (required <= 0) {
                    continue;
                }
                rows.add(ToonBuilder.shiftIdFor(concreteShift)
                        + "," + role.name()
                        + "," + required*2
                        + "," + serializeLongList(doctorIds));
            }
        }

        StringBuilder block = new StringBuilder();
        block.append("role_validation_scratchpad[")
                .append(rows.size())
                .append("]{shift_id,required_role,required_count,candidate_doctor_ids}:\n");
        for (String row : rows) {
            block.append(row).append("\n");
        }
        return block.toString();
    }

    private Map<Seniority, Integer> calculateRequiredByRole(Shift shift) {
        Map<Seniority, Integer> requiredByRole = new EnumMap<>(Seniority.class);
        for (Seniority role : ROLE_ORDER) {
            requiredByRole.put(role, 0);
        }
        if (shift == null || shift.getQuantityShiftSeniority() == null) {
            return requiredByRole;
        }
        for (QuantityShiftSeniority quantityShiftSeniority : shift.getQuantityShiftSeniority()) {
            Map<Seniority, Integer> seniorityMap = quantityShiftSeniority == null
                    ? null
                    : quantityShiftSeniority.getSeniorityMap();
            if (seniorityMap == null) {
                continue;
            }
            for (Seniority role : ROLE_ORDER) {
                int current = requiredByRole.get(role);
                int delta = nonNegativeValue(seniorityMap.get(role));
                requiredByRole.put(role, current + delta);
            }
        }
        return requiredByRole;
    }

    private Map<Seniority, List<Long>> mapCandidateDoctorIdsByRole(List<Doctor> doctors) {
        Map<Seniority, List<Long>> candidatesByRole = new EnumMap<>(Seniority.class);
        for (Seniority role : ROLE_ORDER) {
            candidatesByRole.put(role, new ArrayList<>());
        }
        if (doctors == null) {
            return candidatesByRole;
        }
        for (Doctor doctor : doctors) {
            if (doctor == null || doctor.getId() == null || doctor.getSeniority() == null) {
                continue;
            }
            List<Long> ids = candidatesByRole.get(doctor.getSeniority());
            if (ids != null) {
                ids.add(doctor.getId());
            }
        }
        for (Seniority role : ROLE_ORDER) {
            candidatesByRole.get(role).sort(Long::compareTo);
        }
        return candidatesByRole;
    }

    private int nonNegativeValue(Integer value) {
        return value == null || value < 0 ? 0 : value;
    }

    private String serializeLongList(List<Long> values) {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (int i = 0; i < values.size(); i++) {
            if (i > 0) {
                sb.append(",");
            }
            sb.append(values.get(i));
        }
        sb.append("]");
        return sb.toString();
    }
}
