package org.cswteams.ms3.entity.vincoli;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;

import org.cswteams.ms3.entity.AssegnazioneTurno;

import javax.persistence.Entity;

import java.util.List;

@Entity
public abstract class VincoloAssegnazioneTurnoTurno extends Vincolo{

    /**
     * Controlla se aTurno2 inizia nello stesso orario in cui finisce aTurno1
     */
    protected boolean verificaContiguitàAssegnazioneTurni(AssegnazioneTurno aTurno1, AssegnazioneTurno aTurno2) {

        return verificaContiguitàAssegnazioneTurni(aTurno1, aTurno2, ChronoUnit.MINUTES, 0);
    }

    /**
     * Controlla se aTurno2 inizia nello stesso orario in cui finisce aTurno1,
     * ammettendo un errore pari a delta
     * @param aTurno1
     * @param aTurno2 deve essere successivo temporalmente ad aTurno1
     * @param tu unità di misura temporale per delta (minuti, anni, ...)
     * @param delta numero di unità temporali tollerabili per considerare i turni contigui
     * @return
     */
    protected boolean verificaContiguitàAssegnazioneTurni(AssegnazioneTurno aTurno1, AssegnazioneTurno aTurno2, TemporalUnit tu, long delta){
        LocalDateTime aTurno1End = aTurno1.getData().atTime(aTurno1.getTurno().getOraFine());

        // if aTurno1 shift spans more than one day, we add 1 day to its endDateTime
        if(aTurno1.getTurno().isGiornoSuccessivo()){
            aTurno1End = aTurno1End.plusDays(1);
        }

        LocalDateTime aTurno2Start = aTurno2.getData().atTime(aTurno2.getTurno().getOraInizio());

        return aTurno1End.until(aTurno2Start, tu) <= delta;

    }

    protected int getAssegnazioneTurnoPrecedenteIdx(List<AssegnazioneTurno> turniAssegnati, AssegnazioneTurno turnoDaAssegnare){
        for(int i = 0; i < turniAssegnati.size(); i++){
            if(turniAssegnati.get(i).getData().isAfter(turnoDaAssegnare.getData()) || turniAssegnati.get(i).getData().isEqual(turnoDaAssegnare.getData())){
                if(turniAssegnati.get(i).getTurno().getOraInizio().isAfter(turnoDaAssegnare.getTurno().getOraInizio()) || turniAssegnati.get(i).getTurno().getOraInizio().equals(turnoDaAssegnare.getTurno().getOraInizio())) {
                    return i - 1;
                }
            }
        }
        return turniAssegnati.size()-1;
    }

}
