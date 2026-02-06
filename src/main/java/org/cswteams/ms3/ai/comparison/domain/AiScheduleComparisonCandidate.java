package org.cswteams.ms3.ai.comparison.domain;

import org.cswteams.ms3.ai.decision.AiScheduleCandidateMetrics;

import java.util.Objects;

public class AiScheduleComparisonCandidate {

    private final String candidateId;
    private final Long scheduleId;
    private final ScheduleCandidateType type;
    private final String rawScheduleText;
    private final DecisionMetricValues rawMetrics;
    private final AiScheduleCandidateMetrics normalizedMetrics;

    public AiScheduleComparisonCandidate(String candidateId,
                                         Long scheduleId,
                                         ScheduleCandidateType type,
                                         String rawScheduleText,
                                         DecisionMetricValues rawMetrics,
                                         AiScheduleCandidateMetrics normalizedMetrics) {
        this.candidateId = candidateId;
        this.scheduleId = scheduleId;
        this.type = Objects.requireNonNull(type, "type");
        this.rawScheduleText = rawScheduleText;
        this.rawMetrics = rawMetrics;
        this.normalizedMetrics = normalizedMetrics;
    }

    public String getCandidateId() {
        return candidateId;
    }

    public Long getScheduleId() {
        return scheduleId;
    }

    public ScheduleCandidateType getType() {
        return type;
    }

    public String getRawScheduleText() {
        return rawScheduleText;
    }

    public DecisionMetricValues getRawMetrics() {
        return rawMetrics;
    }

    public AiScheduleCandidateMetrics getNormalizedMetrics() {
        return normalizedMetrics;
    }
}
