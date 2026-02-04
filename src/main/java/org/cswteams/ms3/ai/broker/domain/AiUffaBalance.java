package org.cswteams.ms3.ai.broker.domain;

public class AiUffaBalance {

    private final AiStdDev nightShiftStdDev;

    public AiUffaBalance(AiStdDev nightShiftStdDev) {
        this.nightShiftStdDev = nightShiftStdDev;
    }

    public AiStdDev getNightShiftStdDev() {
        return nightShiftStdDev;
    }
}
