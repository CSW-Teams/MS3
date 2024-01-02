package org.cswteams.ms3.entity.vincoli;

import lombok.Data;
import org.cswteams.ms3.entity.AssegnazioneTurno;
import org.cswteams.ms3.entity.UserScheduleState;

@Data
public class ContestoVincolo {

    private UserScheduleState userScheduleState;

    private AssegnazioneTurno assegnazioneTurno;

    public ContestoVincolo(UserScheduleState utente, AssegnazioneTurno turno){
        this.assegnazioneTurno = turno;
        this.userScheduleState = utente;
    }

}
