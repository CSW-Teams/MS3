package org.cswteams.ms3.exception;

import org.cswteams.ms3.entity.AssegnazioneTurno;
import org.cswteams.ms3.entity.CategoriaUtente;

public class ViolatedVincoloAssegnazioneTurnoTurnoException extends ViolatedConstraintException{
    public ViolatedVincoloAssegnazioneTurnoTurnoException(AssegnazioneTurno turno1) {
        super(String.format("Il turno %s del giorno %s non è compatibile con quelli assegnati precedentemente", turno1.getTurno().getTipologiaTurno(), turno1.getData().toString()));
    }
}
