package org.cswteams.ms3.dto;

import lombok.Data;
import org.cswteams.ms3.enums.TimeSlot;

import java.util.Set;

@Data
public class RegisterConcreteShiftDTO {


    private int day;
    private int month;
    private int year;

    private TimeSlot timeSlot;

    private Set<DoctorDTO> onDutyDoctors;
    private Set<DoctorDTO> onCallDoctors;
    private Set<MedicalServiceDTO> services;
    private boolean forced;

    public RegisterConcreteShiftDTO(){}

    public void setForced(boolean forced) {
        this.forced = forced;
    }


}
