package org.cswteams.ms3.ai.comparison.dto;

public class AiScheduleSelectionRequestDto {

    private String candidateId;

    public AiScheduleSelectionRequestDto() {
    }

    public AiScheduleSelectionRequestDto(String candidateId) {
        this.candidateId = candidateId;
    }

    public String getCandidateId() {
        return candidateId;
    }

    public void setCandidateId(String candidateId) {
        this.candidateId = candidateId;
    }
}
