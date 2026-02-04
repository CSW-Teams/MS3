package org.cswteams.ms3.entity.constraint;

import org.cswteams.ms3.entity.ConcreteShift;
import org.cswteams.ms3.entity.Doctor;
import org.cswteams.ms3.entity.Holiday;
import org.cswteams.ms3.exception.ViolatedConstraintException;
import org.cswteams.ms3.exception.ViolatedConstraintHolidayException;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Implementa il vincolo {@code ConstraintHoliday}, che evita che un medico venga assegnato
 * alla stessa festività già coperta l'anno precedente, utilizzando la mappa delle festività per medico
 * ({@link DoctorHolidays}).
 *
 * Fa parte del "Catalogo vincoli attivi" (Microtask 1.2).
 *
 * @see docs/AI_powered_rescheduling/sprint_4/story_1.md#microtask-12--vincoli-e-pipeline-priorità-baseline
 */
@Entity
public class ConstraintHoliday extends Constraint {

    @Override
    public void verifyConstraint(ContextConstraint context) throws ViolatedConstraintException {
        Doctor doctor = context.getDoctorUffaPriority().getDoctor();
        ConcreteShift concreteShift = context.getConcreteShift();

        for(Holiday holiday: context.getHolidays()) {
            //check if the concrete shift corresponds to a holiday
            if(concreteShift.getDate() >= holiday.getStartDateEpochDay() && concreteShift.getDate() <= holiday.getEndDateEpochDay()) {
                // [BASELINE-FLOW] CRITICITÀ NULL-CHECK:
                // Qui context.getDoctorHolidays() viene dereferenziato senza un null-check esplicito.
                // Se non ci sono record in DoctorHolidays per il medico, si può generare una NullPointerException.
                for(Holiday holidayDh: context.getDoctorHolidays().getHolidayMap().keySet()) {
                    if(holiday.getName().equals(holidayDh.getName()) && context.getDoctorHolidays().getHolidayMap().get(holidayDh))
                        throw new ViolatedConstraintHolidayException(concreteShift, doctor);

                }

            }

        }

    }


}
