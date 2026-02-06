package org.cswteams.ms3.ai.protocol.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.LinkedHashMap;
import java.util.Map;

public class AiScheduleVariantsResponseDto {
    @JsonProperty("variants")
    public Map<String, AiScheduleResponseDto> variants = new LinkedHashMap<>();
}
