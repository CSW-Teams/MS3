package org.cswteams.ms3.ai.orchestration;

import org.cswteams.ms3.control.toon.ToonBuilder;
import org.cswteams.ms3.entity.ConcreteShift;
import org.cswteams.ms3.entity.QuantityShiftSeniority;
import org.cswteams.ms3.entity.Shift;
import org.cswteams.ms3.enums.Seniority;
import org.cswteams.ms3.enums.TimeSlot;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

@Component
public class AiHardCoveragePromptBlockBuilder {

    private static final String COVERAGE_CHECKLIST = "hard_coverage_checklist[3]:\n"
            + "- ON_DUTY and ON_CALL each must satisfy every per-shift/per-role minimum.\n"
            + "- ON_CALL minimums are identical to ON_DUTY minimums.\n"
            + "- For a given shift_id, one doctor_id cannot appear in both ON_DUTY and ON_CALL.\n";

    public String buildHardCoverageRequirementsBlock(List<ConcreteShift> concreteShifts) {
        List<ConcreteShift> ordered = new ArrayList<>(concreteShifts == null ? List.of() : concreteShifts);
        ordered.sort(Comparator
                .comparingLong(ConcreteShift::getDate)
                .thenComparing(shift -> {
                    Shift concreteShift = shift.getShift();
                    return concreteShift == null || concreteShift.getTimeSlot() == null
                            ? TimeSlot.MORNING
                            : concreteShift.getTimeSlot();
                })
                .thenComparing(shift -> shift.getShift().getId() == null ? 0L : shift.getShift().getId()));

        StringBuilder block = new StringBuilder();
        block.append("hard_coverage_requirements[")
                .append(ordered.size())
                .append("]{shift_id,structured,specialist_junior,specialist_senior,total}:\n");

        for (ConcreteShift concreteShift : ordered) {
            CoverageCounts counts = calculateCoverageCounts(concreteShift == null ? null : concreteShift.getShift());
            block.append(ToonBuilder.shiftIdFor(concreteShift)).append(",")
                    .append(counts.structured).append(",")
                    .append(counts.specialistJunior).append(",")
                    .append(counts.specialistSenior).append(",")
                    .append(counts.total).append("\n");
        }
        block.append(COVERAGE_CHECKLIST);
        return block.toString();
    }

    private CoverageCounts calculateCoverageCounts(Shift shift) {
        int structured = 0;
        int specialistJunior = 0;
        int specialistSenior = 0;

        if (shift != null && shift.getQuantityShiftSeniority() != null) {
            for (QuantityShiftSeniority quantityShiftSeniority : shift.getQuantityShiftSeniority()) {
                Map<Seniority, Integer> seniorityMap = quantityShiftSeniority == null
                        ? null
                        : quantityShiftSeniority.getSeniorityMap();
                if (seniorityMap == null) {
                    continue;
                }
                structured += nonNegativeValue(seniorityMap.get(Seniority.STRUCTURED));
                specialistJunior += nonNegativeValue(seniorityMap.get(Seniority.SPECIALIST_JUNIOR));
                specialistSenior += nonNegativeValue(seniorityMap.get(Seniority.SPECIALIST_SENIOR));
            }
        }

        return new CoverageCounts(structured, specialistJunior, specialistSenior);
    }

    private int nonNegativeValue(Integer value) {
        return value == null || value < 0 ? 0 : value;
    }

    private static class CoverageCounts {
        private final int structured;
        private final int specialistJunior;
        private final int specialistSenior;
        private final int total;

        private CoverageCounts(int structured, int specialistJunior, int specialistSenior) {
            this.structured = structured;
            this.specialistJunior = specialistJunior;
            this.specialistSenior = specialistSenior;
            this.total = structured + specialistJunior + specialistSenior;
        }
    }
}
