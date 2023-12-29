package org.cswteams.ms3.dto;

import lombok.Data;

@Data
public class ModifyConcreteShiftDTO {
    long concreteShiftId;
    long[] onDutyDoctors;
    long[] onCallDoctors;
    long modifyingDoctorId;

    public ModifyConcreteShiftDTO(long concreteShiftId, long[] onDutyDoctors, long[] onCallDoctors, long modifyingDoctorId) {
        this.concreteShiftId = concreteShiftId;
        this.onDutyDoctors = onDutyDoctors;
        this.onCallDoctors = onCallDoctors;
        this.modifyingDoctorId = modifyingDoctorId;
    }

    public ModifyConcreteShiftDTO() {
    }

}
