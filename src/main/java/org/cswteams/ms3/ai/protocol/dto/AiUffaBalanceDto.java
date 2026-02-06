package org.cswteams.ms3.ai.protocol.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AiUffaBalanceDto {
    @JsonProperty("night_shift_std_dev")
    public AiStdDevDto nightShiftStdDev;
}
