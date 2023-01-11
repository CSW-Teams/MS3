package org.cswteams.ms3.control.vincoli;

import lombok.Data;
import org.cswteams.ms3.entity.AssegnazioneTurno;
import org.cswteams.ms3.entity.UserScheduleState;

@Data
public class ContestoVincolo {

    private UserScheduleState userScheduleState;

    private AssegnazioneTurno turno;

    public ContestoVincolo(UserScheduleState utente, AssegnazioneTurno turno){
        this.turno = turno;
        this.userScheduleState = utente;
    }

}
