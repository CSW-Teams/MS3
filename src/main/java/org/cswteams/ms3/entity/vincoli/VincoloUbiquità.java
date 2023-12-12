package org.cswteams.ms3.entity.vincoli;

import org.cswteams.ms3.entity.ConcreteShift;
import org.cswteams.ms3.exception.ViolatedConstraintException;
import org.cswteams.ms3.exception.ViolatedVincoloAssegnazioneTurnoTurnoException;

import javax.persistence.Entity;
import java.time.LocalDateTime;

@Entity
public class VincoloUbiquità extends VincoloAssegnazioneTurnoTurno{

    @Override
    public void verificaVincolo(ContestoVincolo contesto) throws ViolatedConstraintException {
        if(!contesto.getUserScheduleState().getAssegnazioniTurnoCache().isEmpty()){
            for(ConcreteShift turnoAssegnato: contesto.getUserScheduleState().getAssegnazioniTurnoCache()){
                LocalDateTime startA = LocalDateTime.of(turnoAssegnato.getData(),turnoAssegnato.getShift().getOraInizio());
                LocalDateTime startB = LocalDateTime.of(contesto.getConcreteShift().getData(),contesto.getConcreteShift().getShift().getOraInizio());

                LocalDateTime endA =  startA.plus(turnoAssegnato.getShift().getDurata());
                LocalDateTime endB = startB.plus(contesto.getConcreteShift().getShift().getDurata());

                if(!((startA.isBefore(startB) && (endA.isBefore(startB) || endA.isEqual(startB))) || (startB.isBefore(startA) && (endB.isBefore(startA) || endB.isEqual(startA))))){
                    throw new ViolatedVincoloAssegnazioneTurnoTurnoException(contesto.getConcreteShift(), turnoAssegnato, contesto.getUserScheduleState().getDoctor());
                }
            }
        }
    }

}
