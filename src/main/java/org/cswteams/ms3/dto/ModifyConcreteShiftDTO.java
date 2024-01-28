package org.cswteams.ms3.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class ModifyConcreteShiftDTO {

    @NotNull
    Long concreteShiftId;

    long[] onDutyDoctors;
    long[] onCallDoctors;

    @NotNull
    Long modifyingDoctorId;

    public ModifyConcreteShiftDTO(
            @JsonProperty("concreteShiftId") Long concreteShiftId,
            @JsonProperty("onDutyDoctors") long[] onDutyDoctors,
            @JsonProperty("onCallDoctors") long[] onCallDoctors,
            @JsonProperty("modifyingDoctorId") Long modifyingDoctorId) {
        this.concreteShiftId = concreteShiftId;
        this.onDutyDoctors = onDutyDoctors;
        this.onCallDoctors = onCallDoctors;
        this.modifyingDoctorId = modifyingDoctorId;
    }
}
