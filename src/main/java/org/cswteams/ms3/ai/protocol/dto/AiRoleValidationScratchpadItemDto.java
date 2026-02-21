package org.cswteams.ms3.ai.protocol.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

public class AiRoleValidationScratchpadItemDto {
    @JsonProperty("shift_id")
    public String shiftId;
    @JsonProperty("role_required")
    public String roleRequired;
    @JsonProperty("candidate_doctor_ids")
    public List<Integer> candidateDoctorIds = new ArrayList<>();
}
