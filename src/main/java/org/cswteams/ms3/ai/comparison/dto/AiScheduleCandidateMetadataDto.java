package org.cswteams.ms3.ai.comparison.dto;

public class AiScheduleCandidateMetadataDto {

    private final String candidateId;
    private final Long scheduleId;
    private final String type;

    public AiScheduleCandidateMetadataDto(String candidateId, Long scheduleId, String type) {
        this.candidateId = candidateId;
        this.scheduleId = scheduleId;
        this.type = type;
    }

    public String getCandidateId() {
        return candidateId;
    }

    public Long getScheduleId() {
        return scheduleId;
    }

    public String getType() {
        return type;
    }
}
