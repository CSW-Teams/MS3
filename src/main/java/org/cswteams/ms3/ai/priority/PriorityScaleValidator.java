package org.cswteams.ms3.ai.priority;

import org.springframework.stereotype.Component;

import javax.validation.ValidationException;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

@Component
public class PriorityScaleValidator {

    private static final double EPSILON = 0.0001;
    private static final Set<String> REQUIRED_DIMENSIONS = Set.of("business", "software", "operational");
    private static final Set<String> ALLOWED_METRICS = buildAllowedMetrics();

    public void validate(PriorityScaleConfig config) {
        if (config == null) {
            throw new ValidationException("Priority scale config must not be null");
        }
        if (config.getVersion() == null) {
            throw new ValidationException("Priority scale config version is required");
        }
        Map<String, PriorityScaleConfig.DimensionConfig> dimensions = config.getDimensions();
        if (dimensions == null || dimensions.isEmpty()) {
            throw new ValidationException("Priority scale config must define dimensions");
        }
        for (String dimension : REQUIRED_DIMENSIONS) {
            if (!dimensions.containsKey(dimension)) {
                throw new ValidationException("Missing dimension: " + dimension);
            }
        }
        validateDimensionWeights(dimensions);
        for (Map.Entry<String, PriorityScaleConfig.DimensionConfig> entry : dimensions.entrySet()) {
            validateDimension(entry.getKey(), entry.getValue());
        }
    }

    private void validateDimensionWeights(Map<String, PriorityScaleConfig.DimensionConfig> dimensions) {
        double sum = 0.0;
        for (Map.Entry<String, PriorityScaleConfig.DimensionConfig> entry : dimensions.entrySet()) {
            PriorityScaleConfig.DimensionConfig dimension = entry.getValue();
            if (dimension.getWeight() == null) {
                throw new ValidationException("Dimension weight missing for: " + entry.getKey());
            }
            if (dimension.getWeight() < 0) {
                throw new ValidationException("Dimension weight must be >= 0 for: " + entry.getKey());
            }
            sum += dimension.getWeight();
        }
        if (Math.abs(sum - 1.0) > EPSILON) {
            throw new ValidationException("Dimension weights must sum to 1.0, got " + sum);
        }
    }

    private void validateDimension(String name, PriorityScaleConfig.DimensionConfig dimension) {
        if (dimension == null) {
            throw new ValidationException("Dimension config missing for: " + name);
        }
        Map<String, PriorityScaleConfig.MetricConfig> metrics = dimension.getMetrics();
        if (dimension.getWeight() == 0.0 && (metrics == null || metrics.isEmpty())) {
            return;
        }
        if (metrics == null || metrics.isEmpty()) {
            throw new ValidationException("Dimension " + name + " must define metrics");
        }
        double sum = 0.0;
        for (Map.Entry<String, PriorityScaleConfig.MetricConfig> entry : metrics.entrySet()) {
            String metricId = entry.getKey();
            if (!ALLOWED_METRICS.contains(metricId)) {
                throw new ValidationException("Unknown metric: " + metricId);
            }
            PriorityScaleConfig.MetricConfig metric = entry.getValue();
            if (metric == null) {
                throw new ValidationException("Metric config missing for: " + metricId);
            }
            if (metric.getWeight() == null) {
                throw new ValidationException("Metric weight missing for: " + metricId);
            }
            if (metric.getWeight() < 0) {
                throw new ValidationException("Metric weight must be >= 0 for: " + metricId);
            }
            if (metric.getDirection() == null) {
                throw new ValidationException("Metric direction missing for: " + metricId);
            }
            if (metric.getType() == null) {
                throw new ValidationException("Metric type missing for: " + metricId);
            }
            if (metric.getValueType() == null) {
                throw new ValidationException("Metric value type missing for: " + metricId);
            }
            if (metric.getType() == MetricType.HARD_GATE && metric.getThreshold() == null) {
                throw new ValidationException("Hard gate metric requires threshold: " + metricId);
            }
            if (metric.getEnabled() == null) {
                throw new ValidationException("Metric enabled flag missing for: " + metricId);
            }
            if (!metric.getEnabled() && metric.getWeight() > 0) {
                throw new ValidationException("Disabled metric must have weight 0: " + metricId);
            }
            if (metric.getValueType() == MetricValueType.BOOLEAN) {
                validateBooleanMetric(metricId, metric);
            } else {
                if (metric.getBooleanScoreTrue() != null || metric.getBooleanScoreFalse() != null) {
                    throw new ValidationException("Numeric metric must not define boolean scores: " + metricId);
                }
            }
            sum += metric.getWeight();
        }
        if (Math.abs(sum - 1.0) > EPSILON) {
            throw new ValidationException("Metric weights for dimension " + name + " must sum to 1.0, got " + sum);
        }
    }

    private void validateBooleanMetric(String metricId, PriorityScaleConfig.MetricConfig metric) {
        if (metric.getBooleanScoreTrue() == null || metric.getBooleanScoreFalse() == null) {
            throw new ValidationException("Boolean metric must define boolean scores: " + metricId);
        }
        if (metric.getDirection() != MetricDirection.HIGHER_IS_BETTER) {
            throw new ValidationException("Boolean metric direction must be HIGHER_IS_BETTER: " + metricId);
        }
    }

    private static Set<String> buildAllowedMetrics() {
        Set<String> metrics = new LinkedHashSet<>();
        for (int i = 1; i <= 6; i++) {
            metrics.add("M1." + i);
        }
        for (int i = 1; i <= 25; i++) {
            metrics.add("M2." + i);
        }
        for (int i = 1; i <= 5; i++) {
            metrics.add("M3." + i);
        }
        return metrics;
    }
}
