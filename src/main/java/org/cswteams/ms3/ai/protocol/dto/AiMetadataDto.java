package org.cswteams.ms3.ai.protocol.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AiMetadataDto {
    public String reasoning;
    @JsonProperty("algorithm")
    public String algorithm;
    @JsonProperty("generation_time")
    public String generationTime;
    @JsonProperty("optimality_score")
    public Double optimalityScore;
    public AiMetricsDto metrics;
    @JsonProperty("role_validation_scratchpad")
    public List<AiRoleValidationScratchpadItemDto> roleValidationScratchpad = new ArrayList<>();
}
