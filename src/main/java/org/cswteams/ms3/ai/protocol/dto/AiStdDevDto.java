package org.cswteams.ms3.ai.protocol.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AiStdDevDto {
    public Double initial;
    @JsonProperty("final")
    public Double finalValue;
}
