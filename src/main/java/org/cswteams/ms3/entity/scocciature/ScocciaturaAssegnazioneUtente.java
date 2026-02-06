package org.cswteams.ms3.entity.scocciature;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.cswteams.ms3.enums.TimeSlot;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.time.DayOfWeek;
import java.time.LocalDate;

/**

 * Rappresenta una {@link Scocciatura} che calcola un peso (penalità) per un medico assegnato a un

 * {@link org.cswteams.ms3.entity.ConcreteShift turno concreto} basandosi sul {@link DayOfWeek giorno della settimana}

 * e sulla {@link TimeSlot tipologia del turno}.

 *

 * Le "scocciature" sono entità persistenti che generano i delta di priorità UFFA.

 * (Microtask 1.2 - Pipeline priorità UFFA/scocciatura).

 *

 * @see docs/AI_powered_rescheduling/sprint_4/story_1.md#microtask-12--vincoli-e-pipeline-priorità-baseline

 */

@Entity

@Getter

@EqualsAndHashCode(callSuper = true)

public class ScocciaturaAssegnazioneUtente extends Scocciatura {



    /** Il peso (penalità) di questa scocciatura. */
    private int peso;

    /** Il {@link DayOfWeek giorno della settimana} a cui si applica questa scocciatura. */
    @Column(name = "giorno_settimana")
    private DayOfWeek giornoSettimana;

    /** Il {@link TimeSlot tipo di turno} a cui si applica questa scocciatura. */
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

    /**
     * Calcola il valore di "uffa" (penalità) per l'assegnazione di un medico,
     * se il {@link org.cswteams.ms3.entity.ConcreteShift turno concreto} nel {@link ContestoScocciatura contesto}
     * corrisponde al giorno della settimana e al tipo di turno specificati da questa scocciatura.
     * @param contesto Il {@link ContestoScocciatura contesto} dell'assegnazione.
     * @return Il peso della scocciatura se le condizioni sono soddisfatte, altrimenti 0.
     */
    @Override
    public int calcolaUffa(ContestoScocciatura contesto) {

        TimeSlot timeSlot = contesto.getConcreteShift().getShift().getTimeSlot();
        if(LocalDate.ofEpochDay(contesto.getConcreteShift().getDate()).getDayOfWeek().equals(this.giornoSettimana) && timeSlot.equals(this.timeSlot))
            return this.peso;

        return 0;
    }
}
