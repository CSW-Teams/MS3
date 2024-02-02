package org.cswteams.ms3.entity.constraint;

import org.cswteams.ms3.entity.ConcreteShift;
import org.cswteams.ms3.exception.ViolatedConstraintException;
import org.cswteams.ms3.exception.ViolatedVincoloAssegnazioneTurnoTurnoException;

import javax.persistence.Entity;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
public class ConstraintUbiquita extends ConstraintAssegnazioneTurnoTurno {

    /**
     * This method checks if Ubiquita constraint is respected while inserting a new concrete shift into a schedule.
     * @param context Object comprehending the new concrete shift to be assigned and the information about doctor's state in the corresponding schedule
     * @throws ViolatedConstraintException Exception thrown if the constraint is violated
     */
    @Override
    public void verifyConstraint(ContextConstraint context) throws ViolatedConstraintException {

        if(!context.getDoctorXY().getAssegnazioniTurnoCache().isEmpty()){
            for(ConcreteShift assignedConcreteShift: context.getDoctorXY().getAssegnazioniTurnoCache()){
                LocalDate dateStartA = LocalDate.ofEpochDay(assignedConcreteShift.getDate());   //conversion Epoch -> LocalDate of assignedConcreteShift.getDate()
                LocalDateTime startA = dateStartA.atTime(assignedConcreteShift.getShift().getStartTime());

                LocalDate dateStartB = LocalDate.ofEpochDay(context.getConcreteShift().getDate());   //conversion Epoch -> LocalDate of context.getConcreteShift().getDate()
                LocalDateTime startB = dateStartB.atTime(context.getConcreteShift().getShift().getStartTime());

                LocalDateTime endA = startA.plus(assignedConcreteShift.getShift().getDuration());
                LocalDateTime endB = startB.plus(context.getConcreteShift().getShift().getDuration());

                if(!((startA.isBefore(startB) && (endA.isBefore(startB) || endA.isEqual(startB))) || (startB.isBefore(startA) && (endB.isBefore(startA) || endB.isEqual(startA))))){
                    throw new ViolatedVincoloAssegnazioneTurnoTurnoException(context.getConcreteShift(), assignedConcreteShift, context.getDoctorXY().getDoctor());
                }
            }
        }

    }

}
