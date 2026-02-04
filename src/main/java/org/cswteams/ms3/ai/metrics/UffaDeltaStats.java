package org.cswteams.ms3.ai.metrics;

public class UffaDeltaStats {

    private final double mean;
    private final double variance;
    private final double min;
    private final double max;
    private final double coefficientOfVariation;

    public UffaDeltaStats(double mean,
                          double variance,
                          double min,
                          double max,
                          double coefficientOfVariation) {
        this.mean = mean;
        this.variance = variance;
        this.min = min;
        this.max = max;
        this.coefficientOfVariation = coefficientOfVariation;
    }

    public double getMean() {
        return mean;
    }

    public double getVariance() {
        return variance;
    }

    public double getMin() {
        return min;
    }

    public double getMax() {
        return max;
    }

    public double getCoefficientOfVariation() {
        return coefficientOfVariation;
    }
}
