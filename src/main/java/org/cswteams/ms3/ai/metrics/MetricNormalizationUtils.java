package org.cswteams.ms3.ai.metrics;

public final class MetricNormalizationUtils {

    private MetricNormalizationUtils() {
    }

    public static double normalizeUpValue(double value, double min, double max) {
        validateBounds(min, max);
        if (value < min || value > max) {
            throw new IllegalArgumentException("Value " + value + " outside bounds [" + min + ", " + max + "]");
        }
        return (value - min) / (max - min);
    }

    public static double deltaNormalizedUp(double current, double previous, double min, double max) {
        double currentNormalized = normalizeUpValue(current, min, max);
        double previousNormalized = normalizeUpValue(previous, min, max);
        return currentNormalized - previousNormalized;
    }

    private static void validateBounds(double min, double max) {
        if (Double.isNaN(min) || Double.isNaN(max)) {
            throw new IllegalArgumentException("Bounds cannot be NaN");
        }
        if (max <= min) {
            throw new IllegalArgumentException("Max bound must be greater than min bound");
        }
    }
}
