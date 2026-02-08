package org.cswteams.ms3.ai.comparison.dto;

import java.util.Collections;
import java.util.List;

public class AiScheduleComparisonResponseDto {

    private final List<AiScheduleComparisonCandidateDto> candidates;
    private final AiScheduleDecisionOutcomeDto decisionOutcome;
    private final String generationStatus;
    private final String metricsSpecId;
    private final String errorType;
    private final String errorCode;
    private final String failureStage;
    private final boolean retryable;

    public AiScheduleComparisonResponseDto(List<AiScheduleComparisonCandidateDto> candidates,
                                           AiScheduleDecisionOutcomeDto decisionOutcome,
                                           String metricsSpecId) {
        this(candidates, decisionOutcome, "success", metricsSpecId, null, null, null, false);
    }

    public AiScheduleComparisonResponseDto(List<AiScheduleComparisonCandidateDto> candidates,
                                           AiScheduleDecisionOutcomeDto decisionOutcome,
                                           String generationStatus,
                                           String metricsSpecId,
                                           String errorType,
                                           String errorCode,
                                           String failureStage,
                                           boolean retryable) {
        this.candidates = candidates == null ? Collections.emptyList() : Collections.unmodifiableList(candidates);
        this.decisionOutcome = decisionOutcome;
        this.generationStatus = generationStatus;
        this.metricsSpecId = metricsSpecId;
        this.errorType = errorType;
        this.errorCode = errorCode;
        this.failureStage = failureStage;
        this.retryable = retryable;
    }

    public List<AiScheduleComparisonCandidateDto> getCandidates() {
        return candidates;
    }

    public AiScheduleDecisionOutcomeDto getDecisionOutcome() {
        return decisionOutcome;
    }

    public String getGenerationStatus() {
        return generationStatus;
    }

    public String getMetricsSpecId() {
        return metricsSpecId;
    }

    public String getErrorType() {
        return errorType;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public String getFailureStage() {
        return failureStage;
    }

    public boolean isRetryable() {
        return retryable;
    }
}
