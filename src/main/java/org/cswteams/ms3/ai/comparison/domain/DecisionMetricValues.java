package org.cswteams.ms3.ai.comparison.domain;

import org.cswteams.ms3.ai.metrics.SentimentTransitionCounts;

public class DecisionMetricValues {

    private final Double coverage;
    private final Double uffaBalance;
    private final Double sentimentTransitions;
    private final SentimentTransitionCounts sentimentTransitionCounts;
    private final Double upDelta;
    private final Double varianceDelta;

    public DecisionMetricValues(Double coverage,
                                Double uffaBalance,
                                Double sentimentTransitions,
                                Double upDelta,
                                Double varianceDelta) {
        this(coverage, uffaBalance, sentimentTransitions, null, upDelta, varianceDelta);
    }

    public DecisionMetricValues(Double coverage,
                                Double uffaBalance,
                                Double sentimentTransitions,
                                SentimentTransitionCounts sentimentTransitionCounts,
                                Double upDelta,
                                Double varianceDelta) {
        this.coverage = coverage;
        this.uffaBalance = uffaBalance;
        this.sentimentTransitions = sentimentTransitions;
        this.sentimentTransitionCounts = sentimentTransitionCounts;
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

    public SentimentTransitionCounts getSentimentTransitionCounts() {
        return sentimentTransitionCounts;
    }

    public Double getUpDelta() {
        return upDelta;
    }

    public Double getVarianceDelta() {
        return varianceDelta;
    }
}
