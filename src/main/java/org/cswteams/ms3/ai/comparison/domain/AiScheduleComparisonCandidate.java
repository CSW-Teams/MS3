package org.cswteams.ms3.ai.comparison.domain;

import org.cswteams.ms3.ai.decision.AiScheduleCandidateMetrics;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class AiScheduleComparisonCandidate {

    private final String candidateId;
    private final Long scheduleId;
    private final ScheduleCandidateType type;
    private final String rawScheduleText;
    private final DecisionMetricValues rawMetrics;
    private final AiScheduleCandidateMetrics normalizedMetrics;
    private final boolean valid;
    private final String validationCode;
    private final String validationMessage;
    private final boolean maxRetriesReached;
    private final List<String> validationViolations;

    public AiScheduleComparisonCandidate(String candidateId,
                                         Long scheduleId,
                                         ScheduleCandidateType type,
                                         String rawScheduleText,
                                         DecisionMetricValues rawMetrics,
                                         AiScheduleCandidateMetrics normalizedMetrics) {
        this(candidateId, scheduleId, type, rawScheduleText, rawMetrics, normalizedMetrics, true, null, null, false, Collections.emptyList());
    }

    public AiScheduleComparisonCandidate(String candidateId,
                                         Long scheduleId,
                                         ScheduleCandidateType type,
                                         String rawScheduleText,
                                         DecisionMetricValues rawMetrics,
                                         AiScheduleCandidateMetrics normalizedMetrics,
                                         boolean valid,
                                         String validationCode,
                                         String validationMessage,
                                         boolean maxRetriesReached,
                                         List<String> validationViolations) {
        this.candidateId = candidateId;
        this.scheduleId = scheduleId;
        this.type = Objects.requireNonNull(type, "type");
        this.rawScheduleText = rawScheduleText;
        this.rawMetrics = rawMetrics;
        this.normalizedMetrics = normalizedMetrics;
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


