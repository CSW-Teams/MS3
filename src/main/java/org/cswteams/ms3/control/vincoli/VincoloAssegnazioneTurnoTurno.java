package org.cswteams.ms3.control.vincoli;

import org.cswteams.ms3.entity.AssegnazioneTurno;

public abstract class VincoloAssegnazioneTurnoTurno implements Vincolo{

    protected boolean verificaContiguitàAssegnazioneTurni(AssegnazioneTurno turno1, AssegnazioneTurno turno2) {
        // TODO: Bisogna vedere com'è la nuova modellazione del turno notturno e modificare questo controllo sulle date di conseguenza
        if(turno1.getData().isEqual(turno2.getData())){
            return turno1.getTurno().getOraFine().equals(turno2.getTurno().getOraInizio());
        }
        else return false;
    }

}
