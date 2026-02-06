package org.cswteams.ms3.ai.broker.domain;

public class AiStdDev {

    private final Double initial;
    private final Double finalValue;

    public AiStdDev(Double initial, Double finalValue) {
        this.initial = initial;
        this.finalValue = finalValue;
    }

    public Double getInitial() {
        return initial;
    }

    public Double getFinalValue() {
        return finalValue;
    }
}
