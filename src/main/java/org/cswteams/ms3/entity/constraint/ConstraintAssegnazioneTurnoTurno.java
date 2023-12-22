package org.cswteams.ms3.entity.constraint;

import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;

import org.cswteams.ms3.entity.ConcreteShift;

import javax.persistence.Entity;

import java.util.List;

@Entity
public abstract class ConstraintAssegnazioneTurnoTurno extends Constraint {

    /**
     * Controlla se aTurno2 inizia nello stesso orario in cui finisce aTurno1
     */
    protected boolean verificaContiguitàAssegnazioneTurni(ConcreteShift aTurno1, ConcreteShift aTurno2) {

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
    protected boolean verificaContiguitàAssegnazioneTurni(ConcreteShift aTurno1, ConcreteShift aTurno2, TemporalUnit tu, long delta){

        /*
        TODO: Refactor using EpochDAy format
        LocalDateTime aTurno1Start = aTurno1.getData().atTime(aTurno1.getShift().getStartTime());
        LocalDateTime aTurno1End = aTurno1Start.plus(aTurno1.getShift().getDuration());

        LocalDateTime aTurno2Start = aTurno2.getData().atTime(aTurno2.getShift().getStartTime());
        LocalDateTime aTurno2End = aTurno2Start.plus(aTurno2.getShift().getDuration());

        if (aTurno1Start.isBefore(aTurno2Start)){
            return Math.abs(aTurno1End.until(aTurno2Start, tu)) <= delta;
        }
        else {
            return Math.abs(aTurno2End.until(aTurno1Start, tu)) <= delta;
        }*/
        return false;
    }

    protected int getAssegnazioneTurnoPrecedenteIdx(List<ConcreteShift> turniAssegnati, ConcreteShift turnoDaAssegnare){
                /*
        TODO: Refactor using EpochDAy format
        for(int i = 0; i < turniAssegnati.size(); i++){
            if(turniAssegnati.get(i).getData().isAfter(turnoDaAssegnare.getData()) || turniAssegnati.get(i).getData().isEqual(turnoDaAssegnare.getData())){
                if(turniAssegnati.get(i).getShift().getStartTime().isAfter(turnoDaAssegnare.getShift().getStartTime()) || turniAssegnati.get(i).getShift().getStartTime().equals(turnoDaAssegnare.getShift().getStartTime())) {
                    return i - 1;
                }
            }
        }
        return turniAssegnati.size()-1;*/
        return 0;
    }

}
