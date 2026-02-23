package org.cswteams.ms3.ai.protocol.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.cswteams.ms3.enums.ConcreteShiftDoctorStatus;
import org.cswteams.ms3.enums.Seniority;

public class AiAssignmentDto {
    @JsonProperty("shift_id")
    public String shiftId;
    @JsonProperty("doctor_id")
    public Integer doctorId;
    @JsonProperty("role_covered")
    @JsonDeserialize(using = AiRoleCoveredDeserializer.class)
    public Seniority roleCovered;
    @JsonProperty("is_forced")
    public Boolean isForced;
    @JsonProperty("violation_note")
    public String violationNote;
    @JsonProperty("assignment_status")
    public ConcreteShiftDoctorStatus assignmentStatus;
}
