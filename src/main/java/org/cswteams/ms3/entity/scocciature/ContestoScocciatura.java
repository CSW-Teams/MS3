package org.cswteams.ms3.entity.scocciature;

import lombok.Data;
import org.cswteams.ms3.entity.AssegnazioneTurno;
import org.cswteams.ms3.entity.UserScheduleState;

@Data
public class ContestoScocciatura {

    private UserScheduleState userScheduleState;

    private AssegnazioneTurno assegnazioneTurno;

    public ContestoScocciatura(UserScheduleState utente, AssegnazioneTurno turno){
        this.assegnazioneTurno = turno;
        this.userScheduleState = utente;
    }

}
