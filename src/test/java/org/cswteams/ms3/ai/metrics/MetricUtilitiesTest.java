package org.cswteams.ms3.ai.metrics;

import org.junit.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class MetricUtilitiesTest {

    @Test
    public void normalizeUpValue_shouldScaleToUnitRange() {
        double normalized = MetricNormalizationUtils.normalizeUpValue(5.0, 0.0, 10.0);

        assertEquals(0.5, normalized, 0.0001);
    }

    @Test(expected = IllegalArgumentException.class)
    public void normalizeUpValue_outOfRange_shouldThrow() {
        MetricNormalizationUtils.normalizeUpValue(15.0, 0.0, 10.0);
    }

    @Test
    public void deltaNormalizedUp_shouldComputeDifference() {
        double delta = MetricNormalizationUtils.deltaNormalizedUp(8.0, 4.0, 0.0, 10.0);

        assertEquals(0.4, delta, 0.0001);
    }

    @Test
    public void aggregation_shouldComputeMeanAndVariance() {
        List<Double> values = Arrays.asList(1.0, 2.0, 3.0);

        assertEquals(2.0, MetricAggregationUtils.mean(values), 0.0001);
        assertEquals(2.0 / 3.0, MetricAggregationUtils.variance(values), 0.0001);
    }

    @Test
    public void uffaDeltaStats_shouldAggregateDeltaMetrics() {
        Map<Long, Double> current = new HashMap<>();
        current.put(1L, 5.0);
        current.put(2L, 7.0);
        Map<Long, Double> previous = new HashMap<>();
        previous.put(1L, 4.0);
        previous.put(2L, 10.0);

        UffaDeltaStats stats = MetricAggregationUtils.uffaDeltaStats(current, previous);

        assertEquals(-1.0, stats.getMean(), 0.0001);
        assertEquals(4.0, stats.getVariance(), 0.0001);
        assertEquals(-3.0, stats.getMin(), 0.0001);
        assertEquals(1.0, stats.getMax(), 0.0001);
        assertEquals(2.0, stats.getCoefficientOfVariation(), 0.0001);
    }

    @Test
    public void countSentimentTransitions_shouldTrackAllTransitions() {
        Map<Long, Integer> previous = new HashMap<>();
        previous.put(1L, -1);
        previous.put(2L, -1);
        previous.put(3L, 0);
        previous.put(4L, 0);
        previous.put(5L, 1);
        previous.put(6L, 1);

        Map<Long, Integer> current = new HashMap<>();
        current.put(1L, 0);
        current.put(2L, 1);
        current.put(3L, 1);
        current.put(4L, -1);
        current.put(5L, -1);
        current.put(6L, 0);

        SentimentTransitionCounts counts = MetricAggregationUtils.countSentimentTransitions(previous, current);

        assertEquals(1, counts.getNegativeToNeutral());
        assertEquals(1, counts.getNegativeToPositive());
        assertEquals(1, counts.getNeutralToPositive());
        assertEquals(1, counts.getNeutralToNegative());
        assertEquals(1, counts.getPositiveToNegative());
        assertEquals(1, counts.getPositiveToNeutral());
    }

    @Test(expected = IllegalArgumentException.class)
    public void deltas_mismatchedSeries_shouldThrow() {
        Map<Long, Double> current = new HashMap<>();
        current.put(1L, 5.0);
        Map<Long, Double> previous = new HashMap<>();
        previous.put(2L, 4.0);

        MetricAggregationUtils.deltas(current, previous);
    }

    @Test(expected = IllegalArgumentException.class)
    public void coefficientOfVariation_zeroMean_shouldThrow() {
        MetricAggregationUtils.coefficientOfVariation(0.0, 1.0);
    }
}
