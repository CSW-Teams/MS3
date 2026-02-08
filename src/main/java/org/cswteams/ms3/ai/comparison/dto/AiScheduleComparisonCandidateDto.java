package org.cswteams.ms3.ai.comparison.dto;

public class AiScheduleComparisonCandidateDto {

    private final AiScheduleCandidateMetadataDto metadata;
    private final String rawScheduleText;
    private final AiScheduleDecisionMetricsDto metrics;

    public AiScheduleComparisonCandidateDto(AiScheduleCandidateMetadataDto metadata,
                                            String rawScheduleText,
                                            AiScheduleDecisionMetricsDto metrics) {
        this.metadata = metadata;
        this.rawScheduleText = rawScheduleText;
        this.metrics = metrics;
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
}
