package org.cswteams.ms3.control.vincoli;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;

import org.cswteams.ms3.entity.AssegnazioneTurno;

import java.time.LocalDate;

import java.time.LocalDate;

public abstract class VincoloAssegnazioneTurnoTurno implements Vincolo{

    /**
     * Controlla se aTurno2 inizia nello stesso orario in cui finisce aTurno1
     */
    protected boolean verificaContiguitàAssegnazioneTurni(AssegnazioneTurno aTurno1, AssegnazioneTurno aTurno2) {

        LocalDate data1;
        if(aTurno1.getTurno().isGiornoSuccessivo()){
            // Nel caso di turno notturno
            data1 = aTurno1.getData().plusDays(1);
        } else data1 = aTurno1.getData();
        if(data1.isEqual(aTurno2.getData())){
            return aTurno1.getTurno().getOraFine().equals(aTurno2.getTurno().getOraInizio());
        }
        else return false;
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

}
