package org.cswteams.ms3.ai.comparison.dto;

public class AiScheduleSelectionRequestDto {

    private String candidateId;
    private Long scheduleId;

    public AiScheduleSelectionRequestDto() {
    }

    public AiScheduleSelectionRequestDto(String candidateId, Long scheduleId) {
        this.candidateId = candidateId;
        this.scheduleId = scheduleId;
    }

    public String getCandidateId() {
        return candidateId;
    }

    public void setCandidateId(String candidateId) {
        this.candidateId = candidateId;
    }

    public Long getScheduleId() {
        return scheduleId;
    }

    public void setScheduleId(Long scheduleId) {
        this.scheduleId = scheduleId;
    }
}
