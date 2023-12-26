package org.cswteams.ms3.dto;

import lombok.Data;
import org.cswteams.ms3.dto.user.UserDTO;
import org.cswteams.ms3.enums.TaskEnum;
import org.cswteams.ms3.enums.TimeSlot;

import java.util.Set;

@Data
public class RegistraAssegnazioneTurnoDTO {


    private int giorno;
    private int mese;
    private int anno;

    private TimeSlot timeSlot;

    private TaskEnum mansione;
    private Set<UserDTO> utentiDiGuardia;
    private Set<UserDTO> utentiReperibili;
    private MedicalServiceDTO servizio;
    private boolean forced;

    public RegistraAssegnazioneTurnoDTO(){}

    public void setForced(boolean forced) {
        this.forced = forced;
    }


}
