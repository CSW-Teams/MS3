package org.cswteams.ms3.entity.vincoli;

import org.cswteams.ms3.entity.AssegnazioneTurno;
import org.cswteams.ms3.exception.ViolatedConstraintException;
import org.cswteams.ms3.exception.ViolatedVincoloAssegnazioneTurnoTurnoException;

import javax.persistence.Entity;
import java.time.LocalDateTime;

@Entity
public class VincoloUbiquità extends VincoloAssegnazioneTurnoTurno{

    @Override
    public void verificaVincolo(ContestoVincolo contesto) throws ViolatedConstraintException {
        if(contesto.getUserScheduleState().getAssegnazioniTurnoCache().size()!= 0){
            for(AssegnazioneTurno turnoAssegnato: contesto.getUserScheduleState().getAssegnazioniTurnoCache()){
                LocalDateTime startA = LocalDateTime.of(turnoAssegnato.getData(),turnoAssegnato.getTurno().getOraInizio());
                LocalDateTime startB = LocalDateTime.of(contesto.getAssegnazioneTurno().getData(),contesto.getAssegnazioneTurno().getTurno().getOraInizio());
                LocalDateTime endA = LocalDateTime.of(turnoAssegnato.getTurno().isGiornoSuccessivo()? turnoAssegnato.getData().plusDays(1) : turnoAssegnato.getData(),turnoAssegnato.getTurno().getOraFine());
                LocalDateTime endB = LocalDateTime.of(contesto.getAssegnazioneTurno().getTurno().isGiornoSuccessivo()? contesto.getAssegnazioneTurno().getData().plusDays(1) : contesto.getAssegnazioneTurno().getData(),contesto.getAssegnazioneTurno().getTurno().getOraFine());

                if(!((startA.isBefore(startB) && (endA.isBefore(startB) || endA.isEqual(startB))) || (startB.isBefore(startA) && (endB.isBefore(startA) || endB.isEqual(startA))))){
                    throw new ViolatedVincoloAssegnazioneTurnoTurnoException(contesto.getAssegnazioneTurno(), turnoAssegnato, contesto.getUserScheduleState().getUtente());
                }
            }
        }
    }

}
