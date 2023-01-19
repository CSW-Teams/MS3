package org.cswteams.ms3.exception;

import org.cswteams.ms3.entity.AssegnazioneTurno;
import org.cswteams.ms3.entity.Utente;

public class ViolatedVincoloAssegnazioneTurnoTurnoException extends ViolatedConstraintException{

    public ViolatedVincoloAssegnazioneTurnoTurnoException(AssegnazioneTurno turno1, AssegnazioneTurno turno2, Utente utente) {
        super(String.format("Il turno %s del giorno %s non è compatibile con il turno %s del giorno %s. La violazione del vincolo riguarda l'utente %s %s",
                turno1.getTurno().getTipologiaTurno(), turno1.getData().toString(), turno2.getTurno().getTipologiaTurno(), turno2.getData().toString(),
                utente.getNome(), utente.getCognome()));
    }

    public ViolatedVincoloAssegnazioneTurnoTurnoException(AssegnazioneTurno assegnazioneTurno, Utente utente, int numGiorniPeriodo, long numMinutiMaxPeriodo) {
        super(String.format("Il turno %s del giorno %s non è compatibile con quelli assegnati precedentemente. La violazione del vincolo riguarda il medico %s %s. Negli ultimi %d giorni sono state" +
                " allocate all'utente più ore rispetto al valore massimo di %d ore",
                assegnazioneTurno.getTurno().getTipologiaTurno(), assegnazioneTurno.getData().toString(),
                utente.getNome(), utente.getCognome(),numGiorniPeriodo,numMinutiMaxPeriodo/60));

    }

    public ViolatedVincoloAssegnazioneTurnoTurnoException(AssegnazioneTurno assegnazioneTurno, Utente utente, long maxConsecutiveMinutes) {
        super(String.format("Il turno %s del giorno %s non è compatibile con quelli assegnati precedentemente. La violazione del vincolo riguarda il medico %s %s. Nel corso della giornata" +
                        " sono state allocate all'utente ore contigue che superano il valore massimo di %d ore",
                assegnazioneTurno.getTurno().getTipologiaTurno(), assegnazioneTurno.getData().toString(),
                utente.getNome(), utente.getCognome(),maxConsecutiveMinutes/60));

    }
}
