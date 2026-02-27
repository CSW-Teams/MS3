package org.cswteams.ms3.ai.decision;

import org.cswteams.ms3.ai.priority.PriorityDimension;
import org.cswteams.ms3.ai.priority.PriorityScaleConfig;
import org.cswteams.ms3.audit.selection.AuditSelection;
import org.cswteams.ms3.audit.selection.AuditedSelectionResult;
import org.cswteams.ms3.audit.selection.SelectionAuditEvent;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
public class DecisionAlgorithmServiceImpl implements DecisionAlgorithmService {

    private static final double SCORE_TOLERANCE = 0.000001;
    private static final double MIN_METRIC = 0.0;
    private static final double MAX_METRIC = 1.0;
    private static final List<PriorityDimension> TIE_BREAK_ORDER = Arrays.asList(
            PriorityDimension.COVERAGE,
            PriorityDimension.UFFA_BALANCE,
            PriorityDimension.UP_DELTA,
            PriorityDimension.VARIANCE_DELTA
    );

    private final PriorityScaleConfig priorityScaleConfig;

    public DecisionAlgorithmServiceImpl(PriorityScaleConfig priorityScaleConfig) {
        this.priorityScaleConfig = Objects.requireNonNull(priorityScaleConfig, "priorityScaleConfig");
    }

    @Override
    public AiScheduleCandidateMetrics selectPreferred(List<AiScheduleCandidateMetrics> candidates) {
        if (candidates == null || candidates.isEmpty()) {
            throw new IllegalArgumentException("Candidates list cannot be null or empty");
        }
        Map<PriorityDimension, Double> weights = priorityScaleConfig.getPriorityScale();
        AiScheduleCandidateMetrics best = null;
        double bestScore = Double.NEGATIVE_INFINITY;
        for (AiScheduleCandidateMetrics candidate : candidates) {
            validateCandidate(candidate);
            double score = weightedScore(candidate, weights);
            if (best == null || score > bestScore + SCORE_TOLERANCE) {
                best = candidate;
                bestScore = score;
            } else if (Math.abs(score - bestScore) <= SCORE_TOLERANCE) {
                int comparison = compareByTieBreak(candidate, best);
                if (comparison > 0) {
                    best = candidate;
                    bestScore = score;
                }
            }
        }
        return best;
    }

    @Override
    @AuditSelection("decision_algorithm_select_preferred")
    public AuditedSelectionResult selectPreferredWithAudit(List<AiScheduleCandidateMetrics> candidates) {
        if (candidates == null || candidates.isEmpty()) {
            throw new IllegalArgumentException("Candidates list cannot be null or empty");
        }
        Map<PriorityDimension, Double> weights = priorityScaleConfig.getPriorityScale();
        AiScheduleCandidateMetrics best = null;
        double bestScore = Double.NEGATIVE_INFINITY;
        Map<String, Double> scores = new java.util.HashMap<>();
        for (AiScheduleCandidateMetrics candidate : candidates) {
            validateCandidate(candidate);
            double score = weightedScore(candidate, weights);
            scores.put(candidate.getCandidateId(), score);
            if (best == null || score > bestScore + SCORE_TOLERANCE) {
                best = candidate;
                bestScore = score;
            } else if (Math.abs(score - bestScore) <= SCORE_TOLERANCE) {
                int comparison = compareByTieBreak(candidate, best);
                if (comparison > 0) {
                    best = candidate;
                    bestScore = score;
                }
            }
        }
        if (best == null) {
            throw new IllegalStateException("Unable to select preferred candidate");
        }
        List<SelectionAuditEvent> events = new java.util.ArrayList<>();
        for (AiScheduleCandidateMetrics candidate : candidates) {
            Map<String, Object> reasons = new java.util.LinkedHashMap<>();
            reasons.put("weights", weights);
            reasons.put("metrics", candidate.toMetricMap());
            boolean selected = candidate.getCandidateId().equals(best.getCandidateId());
            events.add(new SelectionAuditEvent(
                    null,
                    candidate.getCandidateId(),
                    candidate.getCandidateId(),
                    scores.get(candidate.getCandidateId()),
                    selected,
                    reasons
            ));
        }
        return new AuditedSelectionResult(best.getCandidateId(), events);
    }

    private double weightedScore(AiScheduleCandidateMetrics candidate, Map<PriorityDimension, Double> weights) {
        Map<PriorityDimension, Double> metrics = candidate.toMetricMap();
        double total = 0.0;
        for (Map.Entry<PriorityDimension, Double> entry : weights.entrySet()) {
            Double metricValue = metrics.get(entry.getKey());
            if (metricValue == null) {
                throw new IllegalArgumentException("Missing metric for dimension " + entry.getKey());
            }
            total += entry.getValue() * metricValue;
        }
        return total;
    }

    private int compareByTieBreak(AiScheduleCandidateMetrics left, AiScheduleCandidateMetrics right) {
        Map<PriorityDimension, Double> leftMetrics = left.toMetricMap();
        Map<PriorityDimension, Double> rightMetrics = right.toMetricMap();
        for (PriorityDimension dimension : TIE_BREAK_ORDER) {
            double leftValue = leftMetrics.get(dimension);
            double rightValue = rightMetrics.get(dimension);
            if (Math.abs(leftValue - rightValue) > SCORE_TOLERANCE) {
                return Double.compare(leftValue, rightValue);
            }
        }
        return 0;
    }

    private void validateCandidate(AiScheduleCandidateMetrics candidate) {
        if (candidate == null) {
            throw new IllegalArgumentException("Candidate cannot be null");
        }
        for (Map.Entry<PriorityDimension, Double> entry : candidate.toMetricMap().entrySet()) {
            Double value = entry.getValue();
            if (value == null) {
                throw new IllegalArgumentException("Metric value cannot be null for " + entry.getKey());
            }
            if (Double.isNaN(value) || Double.isInfinite(value)) {
                throw new IllegalArgumentException("Metric value must be finite for " + entry.getKey());
            }
            if (value < MIN_METRIC || value > MAX_METRIC) {
                throw new IllegalArgumentException("Metric value must be within [0,1] for " + entry.getKey());
            }
        }
    }
}
