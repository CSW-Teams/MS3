package org.cswteams.ms3.entity.constraint;

import java.time.*;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;

import org.cswteams.ms3.entity.ConcreteShift;

import javax.persistence.Entity;
import javax.persistence.Table;

import java.util.List;

/**
 * A condition imposed when generating associations between shifts.
 */
@Entity
public abstract class ConstraintAssegnazioneTurnoTurno extends Constraint {

    /**
     * This method checks if cShift2 starts at the same hour which cShift1 finishes at.
     *
     * @param cShift1 First concrete shift in temporal order
     * @param cShift2 Second concrete shift in temporal order
     * @return Boolean that represents if the check succeeded
     */
    protected boolean verificaContiguitàAssegnazioneTurni(ConcreteShift cShift1, ConcreteShift cShift2) {

        return verificaContiguitàAssegnazioneTurni(cShift1, cShift2, ChronoUnit.MINUTES, 0);
    }

    /**
     * This method checks if cShift2 starts at the same hour which cShift1 finishes at plus a delay equal to delta.
     * The concrete shifts shall not overlap.
     *
     * @param cShift1 First concrete shift in temporal order
     * @param cShift2 Second concrete shift in temporal order
     * @param tu      Measure unit for param delta (e.g. minutes, hours)
     * @param delta   max number of tolerable temporal units in order to consider cShift1 and cShift2 contiguous
     * @return Boolean that represents if the check succeeded
     */
    protected boolean verificaContiguitàAssegnazioneTurni(ConcreteShift cShift1, ConcreteShift cShift2,
                                                          TemporalUnit tu, long delta) {

        LocalDate cShift1Date = LocalDate.ofEpochDay(cShift1.getDate());   //conversion Epoch -> LocalDate of cShift1.getDate()
        LocalDateTime cShift1Start = cShift1Date.atTime(cShift1.getShift().getStartTime());
        LocalDateTime cShift1End = cShift1Start.plus(cShift1.getShift().getDuration());

        LocalDate cShift2Date = LocalDate.ofEpochDay(cShift2.getDate());   //conversion Epoch -> LocalDate of cShift2.getDate()
        LocalDateTime cShift2Start = cShift2Date.atTime(cShift2.getShift().getStartTime());
        LocalDateTime cShift2End = cShift2Start.plus(cShift2.getShift().getDuration());

        if (cShift1Start.isBefore(cShift2Start)) {
            return Math.abs(cShift1End.until(cShift2Start, tu)) < delta;
        } else {
            return Math.abs(cShift2End.until(cShift1Start, tu)) < delta;
        }

    }

    /**
     * This method returns the concrete shift that is before a new concrete shift passed as parameter.
     *
     * @param concreteShifts            List of all the existent concrete shifts
     * @param concreteShiftToBeAssigned The new concrete shift to be assigned
     * @return The existent concrete shift that is before concreteShiftToBeAssigned
     */
    protected int getAssegnazioneTurnoPrecedenteIdx(List<ConcreteShift> concreteShifts, ConcreteShift concreteShiftToBeAssigned) {

        for (int i = 0; i < concreteShifts.size(); i++) {
            //conversion Epoch -> LocalDate of concreteShifts.get(i).getDate() and concreteShiftToBeAssigned.getDate()
            LocalDate cShiftDate = LocalDate.ofEpochDay(concreteShifts.get(i).getDate());
            LocalDate cShiftDateToBeAssignedDate = LocalDate.ofEpochDay(concreteShiftToBeAssigned.getDate());

            if (cShiftDate.isAfter(cShiftDateToBeAssignedDate) || cShiftDate.isEqual(cShiftDateToBeAssignedDate)) {
                if (concreteShifts.get(i).getShift().getStartTime().isAfter(concreteShiftToBeAssigned.getShift().getStartTime()) || concreteShifts.get(i).getShift().getStartTime().equals(concreteShiftToBeAssigned.getShift().getStartTime())) {
                    return i - 1;
                }
            }
        }
        return concreteShifts.size() - 1;

    }

}
