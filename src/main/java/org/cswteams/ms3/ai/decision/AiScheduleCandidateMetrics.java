package org.cswteams.ms3.ai.decision;

import org.cswteams.ms3.ai.priority.PriorityDimension;

import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;

/**
 * Normalized metrics (0..1, higher-is-better) for a candidate schedule.
 */
public class AiScheduleCandidateMetrics {

    private final String candidateId;
    private final double coverage;
    private final double uffaBalance;
    private final double upDelta;
    private final double varianceDelta;

    public AiScheduleCandidateMetrics(String candidateId,
                                      double coverage,
                                      double uffaBalance,
                                      double upDelta,
                                      double varianceDelta) {
        this.candidateId = Objects.requireNonNull(candidateId, "candidateId");
        this.coverage = coverage;
        this.uffaBalance = uffaBalance;
        this.upDelta = upDelta;
        this.varianceDelta = varianceDelta;
    }

    public String getCandidateId() {
        return candidateId;
    }

    public double getCoverage() {
        return coverage;
    }

    public double getUffaBalance() {
        return uffaBalance;
    }

    public double getUpDelta() {
        return upDelta;
    }

    public double getVarianceDelta() {
        return varianceDelta;
    }

    public Map<PriorityDimension, Double> toMetricMap() {
        Map<PriorityDimension, Double> values = new EnumMap<>(PriorityDimension.class);
        values.put(PriorityDimension.COVERAGE, coverage);
        values.put(PriorityDimension.UFFA_BALANCE, uffaBalance);
        values.put(PriorityDimension.UP_DELTA, upDelta);
        values.put(PriorityDimension.VARIANCE_DELTA, varianceDelta);
        return values;
    }
}
