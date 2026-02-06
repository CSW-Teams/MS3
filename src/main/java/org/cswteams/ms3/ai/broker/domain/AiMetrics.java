package org.cswteams.ms3.ai.broker.domain;

public class AiMetrics {

    private final Double coveragePercent;
    private final AiUffaBalance uffaBalance;
    private final Integer softViolationsCount;

    public AiMetrics(Double coveragePercent, AiUffaBalance uffaBalance, Integer softViolationsCount) {
        this.coveragePercent = coveragePercent;
        this.uffaBalance = uffaBalance;
        this.softViolationsCount = softViolationsCount;
    }

    public Double getCoveragePercent() {
        return coveragePercent;
    }

    public AiUffaBalance getUffaBalance() {
        return uffaBalance;
    }

    public Integer getSoftViolationsCount() {
        return softViolationsCount;
    }
}
