package org.cswteams.ms3.ai.comparison.dto;

import java.util.Collections;
import java.util.List;

public class AiScheduleComparisonResponseDto {

    private final List<AiScheduleComparisonCandidateDto> candidates;
    private final AiScheduleDecisionOutcomeDto decisionOutcome;

    public AiScheduleComparisonResponseDto(List<AiScheduleComparisonCandidateDto> candidates,
                                           AiScheduleDecisionOutcomeDto decisionOutcome) {
        this.candidates = candidates == null ? Collections.emptyList() : Collections.unmodifiableList(candidates);
        this.decisionOutcome = decisionOutcome;
    }

    public List<AiScheduleComparisonCandidateDto> getCandidates() {
        return candidates;
    }

    public AiScheduleDecisionOutcomeDto getDecisionOutcome() {
        return decisionOutcome;
    }
}
