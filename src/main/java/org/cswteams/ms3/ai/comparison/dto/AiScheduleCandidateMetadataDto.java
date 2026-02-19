package org.cswteams.ms3.ai.comparison.dto;

public class AiScheduleCandidateMetadataDto {

    private final String candidateId;
    private final Long scheduleId;
    private final String type;
    private final boolean valid;
    private final String validationCode;
    private final String validationMessage;

    public AiScheduleCandidateMetadataDto(String candidateId, Long scheduleId, String type) {
        this(candidateId, scheduleId, type, true, null, null);
    }

    public AiScheduleCandidateMetadataDto(String candidateId,
                                          Long scheduleId,
                                          String type,
                                          boolean valid,
                                          String validationCode,
                                          String validationMessage) {
        this.candidateId = candidateId;
        this.scheduleId = scheduleId;
        this.type = type;
        this.valid = valid;
        this.validationCode = validationCode;
        this.validationMessage = validationMessage;
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

    public boolean isValid() {
        return valid;
    }

    public String getValidationCode() {
        return validationCode;
    }

    public String getValidationMessage() {
        return validationMessage;
    }
}
