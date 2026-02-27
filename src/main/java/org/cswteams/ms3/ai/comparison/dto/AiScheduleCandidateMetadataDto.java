package org.cswteams.ms3.ai.comparison.dto;

import java.util.Collections;
import java.util.List;

public class AiScheduleCandidateMetadataDto {

    private final String candidateId;
    private final Long scheduleId;
    private final String type;
    private final boolean valid;
    private final String validationCode;
    private final String validationMessage;
    private final boolean maxRetriesReached;
    private final List<String> validationViolations;

    public AiScheduleCandidateMetadataDto(String candidateId, Long scheduleId, String type) {
        this(candidateId, scheduleId, type, true, null, null, false, Collections.emptyList());
    }

    public AiScheduleCandidateMetadataDto(String candidateId,
                                          Long scheduleId,
                                          String type,
                                          boolean valid,
                                          String validationCode,
                                          String validationMessage,
                                          boolean maxRetriesReached,
                                          List<String> validationViolations) {
        this.candidateId = candidateId;
        this.scheduleId = scheduleId;
        this.type = type;
        this.valid = valid;
        this.validationCode = validationCode;
        this.validationMessage = validationMessage;
        this.maxRetriesReached = maxRetriesReached;
        this.validationViolations = validationViolations == null ? Collections.emptyList() : Collections.unmodifiableList(validationViolations);
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

    public boolean isMaxRetriesReached() {
        return maxRetriesReached;
    }

    public List<String> getValidationViolations() {
        return validationViolations;
    }
}
