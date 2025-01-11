package org.cswteams.ms3.entity.scocciature;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.cswteams.ms3.enums.TimeSlot;

import javax.persistence.Column;
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

    private int peso;

    @Column(name = "giorno_settimana")
    private DayOfWeek giornoSettimana;

    @Column(name = "time_slot")
    private TimeSlot timeSlot;

    /**
     * Default constructor needed by Lombok
     */
    public ScocciaturaAssegnazioneUtente() {
    }

    public ScocciaturaAssegnazioneUtente(int peso, DayOfWeek giornoSettimana, TimeSlot timeSlot) {
        this.peso = peso;
        this.giornoSettimana = giornoSettimana;
        this.timeSlot = timeSlot;
    }

    @Override
    public int calcolaUffa(ContestoScocciatura contesto) {

        TimeSlot timeSlot = contesto.getConcreteShift().getShift().getTimeSlot();
        if(LocalDate.ofEpochDay(contesto.getConcreteShift().getDate()).getDayOfWeek().equals(this.giornoSettimana) && timeSlot.equals(this.timeSlot))
            return this.peso;

        return 0;
    }
}
