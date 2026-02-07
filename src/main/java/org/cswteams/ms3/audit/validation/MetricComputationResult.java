package org.cswteams.ms3.audit.validation;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class MetricComputationResult {
    private final Map<String, Double> metrics;
    private final List<String> auditedOutcomeIds;

    public MetricComputationResult(Map<String, Double> metrics, List<String> auditedOutcomeIds) {
        this.metrics = metrics == null ? null : Collections.unmodifiableMap(metrics);
        this.auditedOutcomeIds = auditedOutcomeIds == null ? null : Collections.unmodifiableList(auditedOutcomeIds);
    }

    public Map<String, Double> getMetrics() {
        return metrics;
    }

    public List<String> getAuditedOutcomeIds() {
        return auditedOutcomeIds;
    }
}
