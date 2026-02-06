package org.cswteams.ms3.entity.scocciature;


import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.cswteams.ms3.entity.Holiday;
import org.cswteams.ms3.enums.TimeSlot;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;

/**
 * Rappresenta una {@link Scocciatura} che calcola un peso (penalità) per un medico
 * se viene assegnato a un {@link org.cswteams.ms3.entity.ConcreteShift turno concreto}
 * durante una {@link Holiday festività} specifica e in una {@link TimeSlot fascia oraria} definita.
 *
 * Le "scocciature" sono entità persistenti che generano i delta di priorità UFFA.
 * (Microtask 1.2 - Pipeline priorità UFFA/scocciatura).
 *
 * @see docs/AI_powered_rescheduling/sprint_4/story_1.md#microtask-12--vincoli-e-pipeline-priorità-baseline
 */
@Entity
@Getter
@EqualsAndHashCode(callSuper = true)
public class ScocciaturaVacanza extends Scocciatura {

    /** Il peso (penalità) di questa scocciatura. */
    private int peso;
    /** La {@link Holiday festività} a cui si applica questa scocciatura. */
    @ManyToOne
    @JoinColumn(name = "vacanza_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Holiday holiday;

    /** Il {@link TimeSlot tipo di turno} a cui si applica questa scocciatura durante la festività. */
    @Column(name = "time_slot")
    private TimeSlot timeSlot;

    /**
     * Default constructor needed by Lombok
     */
    public ScocciaturaVacanza() {
    }

    public ScocciaturaVacanza(int peso, Holiday holiday, TimeSlot timeSlot) {
        this.peso = peso;
        this.holiday = holiday;
        this.timeSlot = timeSlot;
    }

    /**
     * Calcola il valore di "uffa" (penalità) per l'assegnazione di un medico,
     * se il {@link org.cswteams.ms3.entity.ConcreteShift turno concreto} nel {@link ContestoScocciatura contesto}
     * cade nella {@link Holiday festività} e nel {@link TimeSlot tipo di turno} specificati da questa scocciatura.
     *
     * @param contesto Il {@link ContestoScocciatura contesto} dell'assegnazione.
     * @return Il peso della scocciatura se le condizioni sono soddisfatte, altrimenti 0.
     */
    @Override
    public int calcolaUffa(ContestoScocciatura contesto) {

        TimeSlot timeSlot = contesto.getConcreteShift().getShift().getTimeSlot();

        if(contesto.getConcreteShift().getDate() >= this.holiday.getStartDateEpochDay() &&
                contesto.getConcreteShift().getDate() <= this.holiday.getEndDateEpochDay() && timeSlot.equals(this.timeSlot))
            return this.peso;

        return 0;

    }

}
