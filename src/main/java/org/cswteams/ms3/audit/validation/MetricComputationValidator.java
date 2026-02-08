package org.cswteams.ms3.audit.validation;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class MetricComputationValidator {
    private static final String CODE_RESULT_NULL = "METRICS_RESULT_NULL";
    private static final String CODE_VALIDATION_FAILED = "METRICS_VALIDATION_FAILED";
    private static final String CODE_AUDIT_COVERAGE_MISMATCH = "AUDIT_COVERAGE_MISMATCH";

    public void validateOrThrow(MetricComputationResult result, Set<String> requiredMetricKeys) {
        validateOrThrow(result, requiredMetricKeys, null);
    }

    public void validateOrThrow(MetricComputationResult result,
                                Set<String> requiredMetricKeys,
                                Set<String> expectedOutcomeIds) {
        if (result == null || result.getMetrics() == null) {
            throw MetricValidationException.withViolations(
                    ErrorCategory.APPLICATION_SCHEMA,
                    CODE_RESULT_NULL,
                    "Metric computation result is null",
                    List.of(new ValidationViolation("$.metrics", "metrics must not be null"))
            );
        }

        List<ValidationViolation> violations = new ArrayList<>();
        Map<String, Double> metrics = result.getMetrics();

        if (requiredMetricKeys != null) {
            for (String key : requiredMetricKeys) {
                if (!metrics.containsKey(key)) {
                    violations.add(new ValidationViolation("$.metrics." + key, "missing required metric"));
                }
            }
        }

        for (Map.Entry<String, Double> entry : metrics.entrySet()) {
            Double value = entry.getValue();
            if (value == null) {
                violations.add(new ValidationViolation("$.metrics." + entry.getKey(), "metric value is null"));
                continue;
            }
            if (Double.isNaN(value) || Double.isInfinite(value)) {
                violations.add(new ValidationViolation("$.metrics." + entry.getKey(), "metric value must be finite"));
                continue;
            }
            if ("rating".equals(entry.getKey()) && (value < 1.0 || value > 6.0)) {
                violations.add(new ValidationViolation("$.metrics.rating", "rating must be within [1,6]"));
            }
        }

        if (!violations.isEmpty()) {
            throw MetricValidationException.withViolations(
                    ErrorCategory.BUSINESS_PROTOCOL,
                    CODE_VALIDATION_FAILED,
                    "Metric computation validation failed",
                    violations
            );
        }

        if (expectedOutcomeIds != null && !expectedOutcomeIds.isEmpty()) {
            List<String> audited = result.getAuditedOutcomeIds();
            if (audited == null || !audited.containsAll(expectedOutcomeIds)) {
                throw MetricValidationException.withViolations(
                        ErrorCategory.BUSINESS_PROTOCOL,
                        CODE_AUDIT_COVERAGE_MISMATCH,
                        "Audit coverage mismatch",
                        List.of(new ValidationViolation("$.auditedOutcomeIds", "missing audited outcomes"))
                );
            }
        }
    }
}
