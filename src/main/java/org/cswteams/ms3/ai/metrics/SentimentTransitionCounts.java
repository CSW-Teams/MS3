package org.cswteams.ms3.ai.metrics;

public class SentimentTransitionCounts {

    private final int negativeToNeutral;
    private final int negativeToPositive;
    private final int neutralToPositive;
    private final int neutralToNegative;
    private final int positiveToNegative;
    private final int positiveToNeutral;

    public SentimentTransitionCounts(int negativeToNeutral,
                                     int negativeToPositive,
                                     int neutralToPositive,
                                     int neutralToNegative,
                                     int positiveToNegative,
                                     int positiveToNeutral) {
        this.negativeToNeutral = negativeToNeutral;
        this.negativeToPositive = negativeToPositive;
        this.neutralToPositive = neutralToPositive;
        this.neutralToNegative = neutralToNegative;
        this.positiveToNegative = positiveToNegative;
        this.positiveToNeutral = positiveToNeutral;
    }

    public int getNegativeToNeutral() {
        return negativeToNeutral;
    }

    public int getNegativeToPositive() {
        return negativeToPositive;
    }

    public int getNeutralToPositive() {
        return neutralToPositive;
    }

    public int getNeutralToNegative() {
        return neutralToNegative;
    }

    public int getPositiveToNegative() {
        return positiveToNegative;
    }

    public int getPositiveToNeutral() {
        return positiveToNeutral;
    }
}
