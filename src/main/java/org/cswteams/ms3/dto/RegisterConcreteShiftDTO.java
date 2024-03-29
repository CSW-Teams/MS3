package org.cswteams.ms3.dto;

import lombok.Data;
import org.cswteams.ms3.dto.medicalservice.MedicalServiceDTO;
import org.cswteams.ms3.dto.user.UserCreationDTO;
import org.cswteams.ms3.enums.TaskEnum;
import org.cswteams.ms3.enums.TimeSlot;

import java.util.Set;

@Data
public class RegisterConcreteShiftDTO {


    private int day;
    private int month;
    private int year;

    private TimeSlot timeSlot;

    private Set<UserCreationDTO> onDutyDoctors; // todo maybe this should be a MedicalDoctorInfoDTO's list
    private Set<UserCreationDTO> onCallDoctors; // todo maybe this should be a MedicalDoctorInfoDTO's list
    //private Set<MedicalServiceDTO> services;    //TODO: to be removed?
    private boolean forced;

    //private TaskEnum mansione;  //TODO: to be removed?
    private MedicalServiceDTO servizio;

    public RegisterConcreteShiftDTO(){}

    public void setForced(boolean forced) {
        this.forced = forced;
    }


}
