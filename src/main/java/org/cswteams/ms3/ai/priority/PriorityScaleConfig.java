package org.cswteams.ms3.ai.priority;

import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;

@Component
public class PriorityScaleConfig {

    private static final double SUM_TOLERANCE = 0.000001;

    private final PriorityScaleProperties properties;

    public PriorityScaleConfig(PriorityScaleProperties properties) {
        this.properties = properties;
    }

    public Map<PriorityDimension, Double> getPriorityScale() {
        Map<PriorityDimension, Double> merged = new EnumMap<>(PriorityDimension.class);
        Map<String, Double> defaults = properties.getDefaults();
        if (defaults == null || defaults.isEmpty()) {
            throw new PriorityScaleValidationException("Priority scale defaults are required");
        }
        applyValues(merged, defaults, "defaults");
        Map<String, Double> overrides = properties.getOverrides();
        if (overrides != null && !overrides.isEmpty()) {
            applyValues(merged, overrides, "overrides");
        }
        validateCoverage(merged);
        validateSum(merged);
        return Collections.unmodifiableMap(merged);
    }

    private void applyValues(Map<PriorityDimension, Double> target, Map<String, Double> source, String sourceName) {
        for (Map.Entry<String, Double> entry : source.entrySet()) {
            String key = entry.getKey();
            if (key == null || key.trim().isEmpty()) {
                throw new PriorityScaleValidationException("Priority scale " + sourceName + " contains empty key");
            }
            PriorityDimension dimension = parseDimension(key, sourceName);
            Double weight = entry.getValue();
            if (weight == null) {
                throw new PriorityScaleValidationException("Priority scale " + sourceName + " weight is null for " + key);
            }
            if (Double.isNaN(weight) || Double.isInfinite(weight)) {
                throw new PriorityScaleValidationException("Priority scale " + sourceName + " weight is invalid for " + key);
            }
            if (weight < 0.0) {
                throw new PriorityScaleValidationException("Priority scale " + sourceName + " weight must be non-negative for " + key);
            }
            target.put(dimension, weight);
        }
    }

    private PriorityDimension parseDimension(String key, String sourceName) {
        try {
            return PriorityDimension.valueOf(key.trim());
        } catch (IllegalArgumentException ex) {
            throw new PriorityScaleValidationException("Unknown priority dimension in " + sourceName + ": " + key);
        }
    }

    private void validateCoverage(Map<PriorityDimension, Double> merged) {
        int expected = PriorityDimension.values().length;
        if (merged.size() != expected) {
            throw new PriorityScaleValidationException("Priority scale must define all " + expected + " dimensions");
        }
    }

    private void validateSum(Map<PriorityDimension, Double> merged) {
        double sum = 0.0;
        for (double value : merged.values()) {
            sum += value;
        }
        if (Math.abs(sum - 1.0) > SUM_TOLERANCE) {
            throw new PriorityScaleValidationException("Priority scale weights must sum to 1.0 (current sum=" + sum + ")");
        }
    }
}
