package org.cswteams.ms3.ai.protocol.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AiMetadataDto {
    public String reasoning;
    @JsonProperty("generation_time")
    public String generationTime;
    @JsonProperty("optimality_score")
    public Double optimalityScore;
    public AiMetricsDto metrics;
}
