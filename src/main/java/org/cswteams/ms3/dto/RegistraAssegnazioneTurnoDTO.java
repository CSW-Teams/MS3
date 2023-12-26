package org.cswteams.ms3.dto;

import lombok.Data;
import org.cswteams.ms3.enums.TaskEnum;
import org.cswteams.ms3.enums.TimeSlot;

import java.time.LocalDate;
import java.util.Set;

@Data
public class RegistraAssegnazioneTurnoDTO {


    private int giorno;
    private int mese;
    private int anno;

    private TimeSlot timeSlot;

    private TaskEnum mansione;
    private Set<DoctorDTO> utentiDiGuardia;
    private Set<DoctorDTO> utentiReperibili;
    private MedicalServiceDTO servizio;
    private boolean forced;

    public RegistraAssegnazioneTurnoDTO(){}

    public void setForced(boolean forced) {
        this.forced = forced;
    }


}
