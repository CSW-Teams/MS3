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

    private Set<UserCreationDTO> onDutyDoctors;
    private Set<UserCreationDTO> onCallDoctors;
    private Set<MedicalServiceDTO> services;
    private boolean forced;

    //TODO: to be removed?
    private TaskEnum mansione;
    private MedicalServiceDTO servizio;

    public RegisterConcreteShiftDTO(){}

    public void setForced(boolean forced) {
        this.forced = forced;
    }


}
