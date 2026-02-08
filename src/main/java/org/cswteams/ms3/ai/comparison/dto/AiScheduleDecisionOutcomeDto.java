package org.cswteams.ms3.ai.comparison.dto;

public class AiScheduleDecisionOutcomeDto {

    private final AiScheduleCandidateMetadataDto selectedCandidate;

    public AiScheduleDecisionOutcomeDto(AiScheduleCandidateMetadataDto selectedCandidate) {
        this.selectedCandidate = selectedCandidate;
    }

    public AiScheduleCandidateMetadataDto getSelectedCandidate() {
        return selectedCandidate;
    }
}
