package org.cswteams.ms3.entity.constraint;

import org.cswteams.ms3.entity.ConcreteShift;
import org.cswteams.ms3.exception.ViolatedConstraintException;
import org.cswteams.ms3.exception.ViolatedVincoloAssegnazioneTurnoTurnoException;

import javax.persistence.Entity;
import java.time.LocalDateTime;

@Entity
public class ConstraintUbiquità extends ConstraintAssegnazioneTurnoTurno {

    @Override
    public void verificaVincolo(ContestoVincolo contesto) throws ViolatedConstraintException {
                        /*
        TODO: Refactor using EpochDay format
        if(!contesto.getDoctorScheduleState().getAssegnazioniTurnoCache().isEmpty()){
            for(ConcreteShift turnoAssegnato: contesto.getDoctorScheduleState().getAssegnazioniTurnoCache()){
                LocalDateTime startA = LocalDateTime.of(turnoAssegnato.getData(),turnoAssegnato.getShift().getStartTime());
                LocalDateTime startB = LocalDateTime.of(contesto.getConcreteShift().getData(),contesto.getConcreteShift().getShift().getStartTime());

                LocalDateTime endA =  startA.plus(turnoAssegnato.getShift().getDuration());
                LocalDateTime endB = startB.plus(contesto.getConcreteShift().getShift().getDuration());

                if(!((startA.isBefore(startB) && (endA.isBefore(startB) || endA.isEqual(startB))) || (startB.isBefore(startA) && (endB.isBefore(startA) || endB.isEqual(startA))))){
                    throw new ViolatedVincoloAssegnazioneTurnoTurnoException(contesto.getConcreteShift(), turnoAssegnato, contesto.getDoctorScheduleState().getDoctor());
                }
            }
        }

                         */
    }

}
