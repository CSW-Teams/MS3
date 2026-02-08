package org.cswteams.ms3.ai.protocol.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.cswteams.ms3.ai.protocol.utils.AiStatus;

import java.util.ArrayList;
import java.util.List;

public class AiScheduleResponseDto {
    public AiStatus status;
    public AiMetadataDto metadata;
    public List<AiAssignmentDto> assignments = new ArrayList<>();
    @JsonProperty("uncovered_shifts")
    public List<AiUncoveredShiftDto> uncoveredShifts = new ArrayList<>();
    @JsonProperty("uffa_delta")
    public List<AiUffaDeltaDto> uffaDelta = new ArrayList<>();
}
