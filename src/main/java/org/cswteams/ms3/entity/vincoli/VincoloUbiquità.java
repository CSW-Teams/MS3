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
        if(!contesto.getUserScheduleState().getAssegnazioniTurnoCache().isEmpty()){
            for(AssegnazioneTurno turnoAssegnato: contesto.getUserScheduleState().getAssegnazioniTurnoCache()){
                LocalDateTime startA = LocalDateTime.of(turnoAssegnato.getData(),turnoAssegnato.getTurno().getOraInizio());
                LocalDateTime startB = LocalDateTime.of(contesto.getAssegnazioneTurno().getData(),contesto.getAssegnazioneTurno().getTurno().getOraInizio());

                LocalDateTime endA =  startA.plus(turnoAssegnato.getTurno().getDurata());
                LocalDateTime endB = startB.plus(contesto.getAssegnazioneTurno().getTurno().getDurata());

                if(!((startA.isBefore(startB) && (endA.isBefore(startB) || endA.isEqual(startB))) || (startB.isBefore(startA) && (endB.isBefore(startA) || endB.isEqual(startA))))){
                    throw new ViolatedVincoloAssegnazioneTurnoTurnoException(contesto.getAssegnazioneTurno(), turnoAssegnato, contesto.getUserScheduleState().getDoctor());
                }
            }
        }
    }

}
