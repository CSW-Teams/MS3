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
     * Le assegnazioni turno non devono essere sovrapposte temporalmente.
     * @param aTurno1
     * @param aTurno2 deve essere successivo temporalmente ad aTurno1
     * @param tu unità di misura temporale per delta (minuti, anni, ...)
     * @param delta numero di unità temporali tollerabili per considerare i turni contigui
     * @return
     */
    protected boolean verificaContiguitàAssegnazioneTurni(AssegnazioneTurno aTurno1, AssegnazioneTurno aTurno2, TemporalUnit tu, long delta){

        LocalDateTime aTurno1Start = aTurno1.getData().atTime(aTurno1.getShift().getOraInizio());
        LocalDateTime aTurno1End = aTurno1Start.plus(aTurno1.getShift().getDurata());

        LocalDateTime aTurno2Start = aTurno2.getData().atTime(aTurno2.getShift().getOraInizio());
        LocalDateTime aTurno2End = aTurno2Start.plus(aTurno2.getShift().getDurata());

        if (aTurno1Start.isBefore(aTurno2Start)){
            return Math.abs(aTurno1End.until(aTurno2Start, tu)) <= delta;
        }
        else {
            return Math.abs(aTurno2End.until(aTurno1Start, tu)) <= delta;
        }

    }

    protected int getAssegnazioneTurnoPrecedenteIdx(List<AssegnazioneTurno> turniAssegnati, AssegnazioneTurno turnoDaAssegnare){
        for(int i = 0; i < turniAssegnati.size(); i++){
            if(turniAssegnati.get(i).getData().isAfter(turnoDaAssegnare.getData()) || turniAssegnati.get(i).getData().isEqual(turnoDaAssegnare.getData())){
                if(turniAssegnati.get(i).getShift().getOraInizio().isAfter(turnoDaAssegnare.getShift().getOraInizio()) || turniAssegnati.get(i).getShift().getOraInizio().equals(turnoDaAssegnare.getShift().getOraInizio())) {
                    return i - 1;
                }
            }
        }
        return turniAssegnati.size()-1;
    }

}
