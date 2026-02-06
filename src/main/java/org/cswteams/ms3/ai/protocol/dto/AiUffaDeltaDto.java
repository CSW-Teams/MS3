package org.cswteams.ms3.ai.protocol.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.cswteams.ms3.ai.protocol.utils.AiUffaQueue;

public class AiUffaDeltaDto {
    @JsonProperty("doctor_id")
    public Integer doctorId;
    public AiUffaQueue queue;
    public Integer points;
}
