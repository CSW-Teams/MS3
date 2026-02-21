package org.cswteams.ms3.ai.comparison.dto;

import java.util.Collections;
import java.util.List;

public class AiScheduleComparisonCandidateDto {

    private final AiScheduleCandidateMetadataDto metadata;
    private final String rawScheduleText;
    private final AiScheduleDecisionMetricsDto metrics;
    private final boolean valid;
    private final int attemptCount;
    private final boolean maxRetriesReached;
    private final List<String> validationErrors;

    public AiScheduleComparisonCandidateDto(AiScheduleCandidateMetadataDto metadata,
                                            String rawScheduleText,
                                            AiScheduleDecisionMetricsDto metrics) {
        this(metadata, rawScheduleText, metrics, true, 1, false, Collections.emptyList());
    }

    public AiScheduleComparisonCandidateDto(AiScheduleCandidateMetadataDto metadata,
                                            String rawScheduleText,
                                            AiScheduleDecisionMetricsDto metrics,
                                            boolean valid,
                                            int attemptCount,
                                            boolean maxRetriesReached,
                                            List<String> validationErrors) {
        this.metadata = metadata;
        this.rawScheduleText = rawScheduleText;
        this.metrics = metrics;
        this.valid = valid;
        this.attemptCount = attemptCount;
        this.maxRetriesReached = maxRetriesReached;
        this.validationErrors = validationErrors == null ? Collections.emptyList() : Collections.unmodifiableList(validationErrors);
    }

    public AiScheduleCandidateMetadataDto getMetadata() {
        return metadata;
    }

    public String getRawScheduleText() {
        return rawScheduleText;
    }

    /**
     * Alias per il download JSON completo della schedulazione.
     */
    public String getScheduleJson() {
        return rawScheduleText;
    }

    public AiScheduleDecisionMetricsDto getMetrics() {
        return metrics;
    }

    /**
     * Alias per esporre le metriche decisionali utilizzate dal confronto.
     */
    public AiScheduleDecisionMetricsDto getDecisionMetrics() {
        return metrics;
    }

    public boolean isValid() {
        return valid;
    }

    public int getAttemptCount() {
        return attemptCount;
    }

    public boolean isMaxRetriesReached() {
        return maxRetriesReached;
    }

    public List<String> getValidationErrors() {
        return validationErrors;
    }
}
