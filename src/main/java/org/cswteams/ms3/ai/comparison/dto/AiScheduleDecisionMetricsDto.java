package org.cswteams.ms3.ai.comparison.dto;

public class AiScheduleDecisionMetricsDto {

    private final AiScheduleDecisionMetricValuesDto raw;
    private final AiScheduleDecisionMetricValuesDto normalized;

    public AiScheduleDecisionMetricsDto(AiScheduleDecisionMetricValuesDto raw,
                                        AiScheduleDecisionMetricValuesDto normalized) {
        this.raw = raw;
        this.normalized = normalized;
    }

    public AiScheduleDecisionMetricValuesDto getRaw() {
        return raw;
    }

    public AiScheduleDecisionMetricValuesDto getNormalized() {
        return normalized;
    }
}
