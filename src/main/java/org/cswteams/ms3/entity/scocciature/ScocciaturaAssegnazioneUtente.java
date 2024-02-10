package org.cswteams.ms3.entity.scocciature;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.cswteams.ms3.enums.TimeSlot;

import javax.persistence.Entity;
import java.time.DayOfWeek;
import java.time.LocalDate;

/**
 * Calcola quanto pesa ad un utente essere assegnato ad una assegnazione in base al giorno della settimana
 * e in base alla tipologia del turno.
 */

@Entity
@Getter
@EqualsAndHashCode(callSuper = true)
public class ScocciaturaAssegnazioneUtente extends Scocciatura {

    private int peso;           // per il nuovo algoritmo
    private int pesoUffaPoints; // per il vecchio algoritmo
    private DayOfWeek giornoSettimana;
    private TimeSlot timeSlot;

    /**
     * Default constructor needed by Lombok
     */
    public ScocciaturaAssegnazioneUtente() {
    }

    public ScocciaturaAssegnazioneUtente(int peso, int pesoUffaPoints, DayOfWeek giornoSettimana, TimeSlot timeSlot) {
        this.peso = peso;
        this.pesoUffaPoints = pesoUffaPoints;
        this.giornoSettimana = giornoSettimana;
        this.timeSlot = timeSlot;
    }

    @Override
    public int calcolaUffa(ContestoScocciatura contesto) {

        TimeSlot timeSlot = contesto.getConcreteShift().getShift().getTimeSlot();
        if(LocalDate.ofEpochDay(contesto.getConcreteShift().getDate()).getDayOfWeek().equals(this.giornoSettimana) && timeSlot.equals(this.timeSlot))
            switch (contesto.getSchedulerType()) {
                case SCHEDULER_UFFAPOINTS:
                    return this.pesoUffaPoints;
                case SCHEDULER_UFFAPRIORITY:
                    return this.peso;
                default:
                    throw new RuntimeException("Invalid Scheduler type.");
            }
        return 0;
    }
}
