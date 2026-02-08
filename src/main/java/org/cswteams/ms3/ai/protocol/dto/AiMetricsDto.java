package org.cswteams.ms3.ai.protocol.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AiMetricsDto {
    @JsonProperty("coverage_percent")
    public Double coveragePercent;
    @JsonProperty("uffa_balance")
    public AiUffaBalanceDto uffaBalance;
    @JsonProperty("soft_violations_count")
    public Integer softViolationsCount;
}
