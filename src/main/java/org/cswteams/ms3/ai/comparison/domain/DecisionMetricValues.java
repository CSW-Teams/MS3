package org.cswteams.ms3.ai.comparison.domain;

public class DecisionMetricValues {

    private final Double coverage;
    private final Double uffaBalance;
    private final Double sentimentTransitions;
    private final Double upDelta;
    private final Double varianceDelta;

    public DecisionMetricValues(Double coverage,
                                Double uffaBalance,
                                Double sentimentTransitions,
                                Double upDelta,
                                Double varianceDelta) {
        this.coverage = coverage;
        this.uffaBalance = uffaBalance;
        this.sentimentTransitions = sentimentTransitions;
        this.upDelta = upDelta;
        this.varianceDelta = varianceDelta;
    }

    public Double getCoverage() {
        return coverage;
    }

    public Double getUffaBalance() {
        return uffaBalance;
    }

    public Double getSentimentTransitions() {
        return sentimentTransitions;
    }

    public Double getUpDelta() {
        return upDelta;
    }

    public Double getVarianceDelta() {
        return varianceDelta;
    }
}
