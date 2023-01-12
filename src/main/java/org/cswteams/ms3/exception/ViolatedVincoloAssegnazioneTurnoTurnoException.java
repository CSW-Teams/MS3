package org.cswteams.ms3.exception;

import org.cswteams.ms3.entity.AssegnazioneTurno;

public class ViolatedVincoloAssegnazioneTurnoTurnoException extends ViolatedConstraintException{
    public ViolatedVincoloAssegnazioneTurnoTurnoException(AssegnazioneTurno turno1) {
        super(String.format("Il turno %s del giorno %s non è compatibile con quelli assegnati precedentemente", turno1.getTurno().getTipologiaTurno(), turno1.getData().toString()));
    }

    public ViolatedVincoloAssegnazioneTurnoTurnoException(AssegnazioneTurno turno1, AssegnazioneTurno turno2) {
        super(String.format("Il turno %s del giorno %s non è compatibile con il turno %s del giorno %s", turno1.getTurno().getTipologiaTurno(), turno1.getData().toString(), turno2.getTurno().getTipologiaTurno(), turno2.getData().toString()));
    }
}
