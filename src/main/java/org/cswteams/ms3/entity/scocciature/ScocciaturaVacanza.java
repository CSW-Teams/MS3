package org.cswteams.ms3.entity.scocciature;


import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.cswteams.ms3.entity.Holiday;
import org.cswteams.ms3.enums.TimeSlot;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;

/**
 * Calcola quanto pesa ad un utente essere messo di turno in una vacanza
 */
@Entity
@Getter
@EqualsAndHashCode(callSuper = true)
public class ScocciaturaVacanza extends Scocciatura {

    private int peso;
    @ManyToOne
    @JoinColumn(name = "vacanza_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Holiday holiday;

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

    @Override
    public int calcolaUffa(ContestoScocciatura contesto) {

        TimeSlot timeSlot = contesto.getConcreteShift().getShift().getTimeSlot();

        if(contesto.getConcreteShift().getDate() >= this.holiday.getStartDateEpochDay() &&
                contesto.getConcreteShift().getDate() <= this.holiday.getEndDateEpochDay() && timeSlot.equals(this.timeSlot))
            return this.peso;

        return 0;

    }

}
