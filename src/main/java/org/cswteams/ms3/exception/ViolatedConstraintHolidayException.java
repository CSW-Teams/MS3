package org.cswteams.ms3.exception;

import org.cswteams.ms3.entity.ConcreteShift;
import org.cswteams.ms3.entity.Doctor;

import java.time.LocalDate;

public class ViolatedConstraintHolidayException extends ViolatedConstraintException {

    public ViolatedConstraintHolidayException(ConcreteShift concreteShift, Doctor doctor) {
        /*super(String.format("Il dottor %s %s non rispetta il vincolo sulle vacanze" +
                        " per il turno %s. La violazione riguarda il giorno %s.",
                doctor.getName(), doctor.getLastname(), concreteShift.getShift().getTimeSlot(),
                ConvertitoreData.daStandardVersoTestuale(LocalDate.ofEpochDay(concreteShift.getDate()))));*/

    }

}
