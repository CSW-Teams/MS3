package org.cswteams.ms3.control.vincoli;

import org.cswteams.ms3.entity.AssegnazioneTurno;
import org.cswteams.ms3.entity.Utente;

public class ContestoVincolo {

    private Utente utente;

    private AssegnazioneTurno turno;

    public ContestoVincolo(Utente utente, AssegnazioneTurno turno){
        this.turno = turno;
        this.utente = utente;
    }

    public Utente getUtente() {

        return utente;
    }

    public AssegnazioneTurno getTurno() {
        return turno;
    }
}
