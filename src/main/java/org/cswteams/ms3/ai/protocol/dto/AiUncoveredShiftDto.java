package org.cswteams.ms3.ai.protocol.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AiUncoveredShiftDto {
    @JsonProperty("shift_id")
    public String shiftId;
    public String reason;
}
