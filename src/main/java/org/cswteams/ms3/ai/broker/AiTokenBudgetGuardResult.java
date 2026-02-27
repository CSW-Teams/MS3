package org.cswteams.ms3.ai.broker;

public class AiTokenBudgetGuardResult {

    private final boolean allowed;
    private final int estimatedInputTokens;
    private final int estimatedOutputTokens;
    private final int projectedTpm;
    private final int budgetLimit;

    public AiTokenBudgetGuardResult(boolean allowed,
                                    int estimatedInputTokens,
                                    int estimatedOutputTokens,
                                    int projectedTpm,
                                    int budgetLimit) {
        this.allowed = allowed;
        this.estimatedInputTokens = estimatedInputTokens;
        this.estimatedOutputTokens = estimatedOutputTokens;
        this.projectedTpm = projectedTpm;
        this.budgetLimit = budgetLimit;
    }

    public boolean isAllowed() {
        return allowed;
    }

    public int getEstimatedInputTokens() {
        return estimatedInputTokens;
    }

    public int getEstimatedOutputTokens() {
        return estimatedOutputTokens;
    }

    public int getProjectedTpm() {
        return projectedTpm;
    }

    public int getBudgetLimit() {
        return budgetLimit;
    }
}
