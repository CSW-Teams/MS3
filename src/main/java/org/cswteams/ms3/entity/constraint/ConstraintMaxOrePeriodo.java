package org.cswteams.ms3.entity.constraint;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.cswteams.ms3.entity.ConcreteShift;
import org.cswteams.ms3.exception.ViolatedConstraintException;
import org.cswteams.ms3.exception.ViolatedVincoloAssegnazioneTurnoTurnoException;

import javax.persistence.Entity;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * This class implements the maximum number of minutes that a doctor can work into a specified period.
 */
@Entity
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class ConstraintMaxOrePeriodo extends ConstraintAssegnazioneTurnoTurno {
    @NotNull
    private int numGiorniPeriodo;

    @NotNull
    private long numMinutiMaxPeriodo;

    public ConstraintMaxOrePeriodo() {
    }

    public ConstraintMaxOrePeriodo(int numGiorniPeriodo, long numMinutiMax){
        this.numGiorniPeriodo = numGiorniPeriodo;
        this.numMinutiMaxPeriodo = numMinutiMax;
    }

    /**
     * This method checks if maxOrePeriodo constraint is respected while inserting a new concrete shift into a schedule.
     * @param context Object comprehending the new concrete shift to be assigned and the information about doctor's state in the corresponding schedule
     * @throws ViolatedConstraintException Exception thrown if the constraint is violated
     */
    @Override
    public void verifyConstraint(ContextConstraint context) throws ViolatedConstraintException {

        List<ConcreteShift> concreteShiftList = context.getDoctorUffaPriority().getAssegnazioniTurnoCache();
        if(concreteShiftList != null && !concreteShiftList.isEmpty()) {
            //We find the bounds of the period to be considered in the schedule in which there is the new concrete shift to be assigned.
            long startPeriodDate = context.getDoctorUffaPriority().getSchedule().getStartDate();
            long endPeriodDate = startPeriodDate + numGiorniPeriodo;
            while(endPeriodDate < context.getDoctorUffaPriority().getSchedule().getEndDate()){
                if(context.getConcreteShift().getDate() < endPeriodDate && (context.getConcreteShift().getDate() > startPeriodDate || context.getConcreteShift().getDate() == startPeriodDate)){
                    break;
                }
                startPeriodDate = endPeriodDate;
                endPeriodDate = endPeriodDate + numGiorniPeriodo;
            }

            //We count the number of minutes composing the existent concrete shift assigned to our doctor in the considered period + the number of minutes composing the new concrete shift.
            long totalMinutes = context.getConcreteShift().getShift().getDuration().toMinutes();
            for(ConcreteShift concreteShift: concreteShiftList){
                if(concreteShift.getDate() < endPeriodDate && (concreteShift.getDate() > startPeriodDate || concreteShift.getDate() == startPeriodDate)){
                    totalMinutes += concreteShift.getShift().getDuration().toMinutes();
                    if(totalMinutes > numMinutiMaxPeriodo){
                        throw new ViolatedVincoloAssegnazioneTurnoTurnoException(context.getConcreteShift(), context.getDoctorUffaPriority().getDoctor(), numGiorniPeriodo, numMinutiMaxPeriodo);
                    }
                }
            }

        }

    }
}
