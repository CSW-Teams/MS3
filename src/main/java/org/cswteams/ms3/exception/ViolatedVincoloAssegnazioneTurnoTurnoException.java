package org.cswteams.ms3.exception;

import org.cswteams.ms3.entity.ConcreteShift;
import org.cswteams.ms3.entity.Doctor;

public class ViolatedVincoloAssegnazioneTurnoTurnoException extends ViolatedConstraintException{

    public ViolatedVincoloAssegnazioneTurnoTurnoException(ConcreteShift turno1, ConcreteShift turno2, Doctor doctor) {
       /* super(String.format("Il turno %s %s del giorno %s non è compatibile con il turno %s %s del giorno %s. La violazione del vincolo riguarda l'utente %s %s",
                turno1.getShift().getTimeSlot(), turno1.getShift().getMansione(), ConvertitoreData.daStandardVersoTestuale(turno1.getData().toString()), turno2.getShift().getTimeSlot(), turno2.getShift().getMansione(), ConvertitoreData.daStandardVersoTestuale(turno2.getData().toString()),
                doctor.getName(), doctor.getLastname()));*/
    }

    public ViolatedVincoloAssegnazioneTurnoTurnoException(ConcreteShift concreteShift, Doctor doctor, int numGiorniPeriodo, long numMinutiMaxPeriodo) {
        /*super(String.format("Il turno %s del giorno %s non è compatibile con quelli assegnati precedentemente. La violazione del vincolo riguarda il medico %s %s. Negli ultimi %d giorni sono state" +
                " allocate all'utente più ore rispetto al valore massimo di %d ore",
                concreteShift.getShift().getTimeSlot(), ConvertitoreData.daStandardVersoTestuale(concreteShift.getData().toString()),
                doctor.getName(), doctor.getLastname(),numGiorniPeriodo,numMinutiMaxPeriodo/60));
*/
    }

    public ViolatedVincoloAssegnazioneTurnoTurnoException(ConcreteShift concreteShift, Doctor doctor, long maxConsecutiveMinutes) {
        /*super(String.format("Il turno %s del giorno %s non è compatibile con quelli assegnati precedentemente. La violazione del vincolo riguarda il medico %s %s. Nel corso della giornata" +
                        " sono state allocate all'utente ore contigue che superano il valore massimo di %d ore",
                concreteShift.getShift().getTimeSlot(), ConvertitoreData.daStandardVersoTestuale(concreteShift.getData().toString()),
                doctor.getName(), doctor.getLastname(),maxConsecutiveMinutes/60));
*/
    }
}
