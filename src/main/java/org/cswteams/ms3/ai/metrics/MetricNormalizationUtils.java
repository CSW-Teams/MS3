package org.cswteams.ms3.ai.metrics;

public final class MetricNormalizationUtils {

    private MetricNormalizationUtils() {
    }

    public static double normalizeRange(double value, double min, double max, boolean lowerIsBetter) {
        validateValue(value);
        validateBoundsAllowEqual(min, max);
        if (Double.compare(min, max) == 0) {
            return 1.0;
        }
        if (value < min || value > max) {
            throw new IllegalArgumentException("Value " + value + " outside bounds [" + min + ", " + max + "]");
        }
        double normalized = (value - min) / (max - min);
        if (lowerIsBetter) {
            normalized = 1.0 - normalized;
        }
        return clamp(normalized);
    }

    public static double normalizeUpValue(double value, double min, double max) {
        validateValue(value);
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
        validateBoundsAllowEqual(min, max);
        if (max == min) {
            throw new IllegalArgumentException("Max bound must be greater than min bound");
        }
    }

    private static void validateBoundsAllowEqual(double min, double max) {
        if (Double.isNaN(min) || Double.isNaN(max)) {
            throw new IllegalArgumentException("Bounds cannot be NaN");
        }
        if (Double.isInfinite(min) || Double.isInfinite(max)) {
            throw new IllegalArgumentException("Bounds must be finite");
        }
        if (max < min) {
            throw new IllegalArgumentException("Max bound must be greater than or equal to min bound");
        }
    }

    private static void validateValue(double value) {
        if (Double.isNaN(value) || Double.isInfinite(value)) {
            throw new IllegalArgumentException("Value must be finite");
        }
    }

    private static double clamp(double value) {
        if (value < 0.0) {
            return 0.0;
        }
        if (value > 1.0) {
            return 1.0;
        }
        return value;
    }
}
