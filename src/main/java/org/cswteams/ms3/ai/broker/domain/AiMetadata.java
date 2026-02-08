package org.cswteams.ms3.ai.broker.domain;

public class AiMetadata {

    private final String reasoning;
    private final String algorithm;
    private final Double optimalityScore;
    private final AiMetrics metrics;

    public AiMetadata(String reasoning, Double optimalityScore, AiMetrics metrics) {
        this(reasoning, null, optimalityScore, metrics);
    }

    public AiMetadata(String reasoning, String algorithm, Double optimalityScore, AiMetrics metrics) {
        this.reasoning = reasoning;
        this.algorithm = algorithm;
        this.optimalityScore = optimalityScore;
        this.metrics = metrics;
    }

    public String getReasoning() {
        return reasoning;
    }

    public String getAlgorithm() {
        return algorithm;
    }

    public Double getOptimalityScore() {
        return optimalityScore;
    }

    public AiMetrics getMetrics() {
        return metrics;
    }
}
