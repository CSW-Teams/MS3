package org.cswteams.ms3.ai.metrics;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public final class MetricAggregationUtils {

    private MetricAggregationUtils() {
    }

    public static double mean(List<Double> values) {
        validateValues(values, "values");
        double sum = 0.0;
        for (Double value : values) {
            sum += requireNonNull(value, "values");
        }
        return sum / values.size();
    }

    public static double variance(List<Double> values) {
        validateValues(values, "values");
        double mean = mean(values);
        double sumSquared = 0.0;
        for (Double value : values) {
            double delta = requireNonNull(value, "values") - mean;
            sumSquared += delta * delta;
        }
        return sumSquared / values.size();
    }

    public static double min(List<Double> values) {
        validateValues(values, "values");
        double min = Double.POSITIVE_INFINITY;
        for (Double value : values) {
            min = Math.min(min, requireNonNull(value, "values"));
        }
        return min;
    }

    public static double max(List<Double> values) {
        validateValues(values, "values");
        double max = Double.NEGATIVE_INFINITY;
        for (Double value : values) {
            max = Math.max(max, requireNonNull(value, "values"));
        }
        return max;
    }

    public static List<Double> deltas(Map<Long, Double> current, Map<Long, Double> previous) {
        validateSeries(current, previous);
        List<Long> orderedKeys = new ArrayList<>(current.keySet());
        Collections.sort(orderedKeys);
        List<Double> deltas = new ArrayList<>(orderedKeys.size());
        for (Long key : orderedKeys) {
            Double currentValue = requireNonNull(current.get(key), "current");
            Double previousValue = requireNonNull(previous.get(key), "previous");
            deltas.add(currentValue - previousValue);
        }
        return deltas;
    }

    public static UffaDeltaStats uffaDeltaStats(Map<Long, Double> current, Map<Long, Double> previous) {
        List<Double> deltas = deltas(current, previous);
        double mean = mean(deltas);
        double variance = variance(deltas);
        double min = min(deltas);
        double max = max(deltas);
        double coefficientOfVariation = coefficientOfVariation(mean, variance);
        return new UffaDeltaStats(mean, variance, min, max, coefficientOfVariation);
    }

    public static double coefficientOfVariation(double mean, double variance) {
        if (mean == 0.0) {
            throw new IllegalArgumentException("Mean must be non-zero for coefficient of variation");
        }
        if (variance < 0.0) {
            throw new IllegalArgumentException("Variance cannot be negative");
        }
        return Math.abs(Math.sqrt(variance) / mean);
    }

    public static SentimentTransitionCounts countSentimentTransitions(Map<Long, Integer> previous,
                                                                     Map<Long, Integer> current) {
        validateSeries(previous, current);
        int negativeToNeutral = 0;
        int negativeToPositive = 0;
        int neutralToPositive = 0;
        int neutralToNegative = 0;
        int positiveToNegative = 0;
        int positiveToNeutral = 0;

        for (Long key : current.keySet()) {
            Integer previousValue = requireNonNull(previous.get(key), "previous");
            Integer currentValue = requireNonNull(current.get(key), "current");
            validateSentiment(previousValue);
            validateSentiment(currentValue);
            if (previousValue == -1 && currentValue == 0) {
                negativeToNeutral++;
            } else if (previousValue == -1 && currentValue == 1) {
                negativeToPositive++;
            } else if (previousValue == 0 && currentValue == 1) {
                neutralToPositive++;
            } else if (previousValue == 0 && currentValue == -1) {
                neutralToNegative++;
            } else if (previousValue == 1 && currentValue == -1) {
                positiveToNegative++;
            } else if (previousValue == 1 && currentValue == 0) {
                positiveToNeutral++;
            }
        }

        return new SentimentTransitionCounts(negativeToNeutral, negativeToPositive, neutralToPositive,
                neutralToNegative, positiveToNegative, positiveToNeutral);
    }

    private static void validateSentiment(int value) {
        if (value < -1 || value > 1) {
            throw new IllegalArgumentException("Sentiment must be -1, 0, or 1");
        }
    }

    private static void validateValues(List<Double> values, String name) {
        if (values == null || values.isEmpty()) {
            throw new IllegalArgumentException(name + " cannot be null or empty");
        }
    }

    private static <K, V> void validateSeries(Map<K, V> current, Map<K, V> previous) {
        if (current == null || previous == null) {
            throw new IllegalArgumentException("Series cannot be null");
        }
        if (current.isEmpty() || previous.isEmpty()) {
            throw new IllegalArgumentException("Series cannot be empty");
        }
        if (current.size() != previous.size()) {
            throw new IllegalArgumentException("Series must have the same number of entries");
        }
        for (K key : current.keySet()) {
            if (!previous.containsKey(key)) {
                throw new IllegalArgumentException("Missing previous value for key: " + key);
            }
        }
    }

    private static <T> T requireNonNull(T value, String name) {
        if (value == null) {
            throw new IllegalArgumentException("Null value in " + name + " series");
        }
        return value;
    }
}
