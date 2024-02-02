package org.cswteams.ms3.entity.constraint;

import org.cswteams.ms3.entity.ConcreteShift;
import org.cswteams.ms3.entity.Doctor;
import org.cswteams.ms3.entity.Holiday;
import org.cswteams.ms3.exception.ViolatedConstraintException;
import org.cswteams.ms3.exception.ViolatedConstraintHolidayException;

import javax.persistence.Entity;

/**
 * This <i>constraint</i> is to make sure that a doctor who worked on a certain holiday
 * in the previous year is not assigned to a shift corresponding to the same holiday.
 */
@Entity
public class ConstraintHoliday extends Constraint {

    @Override
    public void verifyConstraint(ContextConstraint context) throws ViolatedConstraintException {
        Doctor doctor = context.getDoctorXY().getDoctor();
        ConcreteShift concreteShift = context.getConcreteShift();

        for(Holiday holiday: context.getHolidays()) {
            //check if the concrete shift corresponds to a holiday
            if(concreteShift.getDate() >= holiday.getStartDateEpochDay() && concreteShift.getDate() <= holiday.getEndDateEpochDay()) {
                for(Holiday holidayDh: context.getDoctorHolidays().getHolidayMap().keySet()) {
                    if(holiday.getName().equals(holidayDh.getName()) && context.getDoctorHolidays().getHolidayMap().get(holidayDh))
                        throw new ViolatedConstraintHolidayException(concreteShift, doctor);

                }

            }

        }

    }


}
