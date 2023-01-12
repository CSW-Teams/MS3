package org.cswteams.ms3.control.vincoli;

import org.cswteams.ms3.entity.AssegnazioneTurno;

import java.time.LocalDate;

public abstract class VincoloAssegnazioneTurnoTurno implements Vincolo{

    protected boolean verificaContiguitàAssegnazioneTurni(AssegnazioneTurno turno1, AssegnazioneTurno turno2) {
        LocalDate data1;
        if(turno1.getTurno().isGiornoSuccessivo()){
            // Nel caso di turno notturno
            data1 = turno1.getData().plusDays(1);
        } else data1 = turno1.getData();
        if(data1.isEqual(turno2.getData())){
            return turno1.getTurno().getOraFine().equals(turno2.getTurno().getOraInizio());
        }
        else return false;
    }

}
