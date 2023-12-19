package org.cswteams.ms3.entity.scocciature;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.cswteams.ms3.entity.Holiday;
import org.cswteams.ms3.enums.TimeSlot;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.time.LocalDate;

/**
 * Calcola quanto pesa ad un utente essere messo di turno in una vacanza
 */

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
public class ScocciaturaVacanza extends Scocciatura {

    private int peso;
    @ManyToOne
    @JoinColumn(name = "vacanza_id")
    private Holiday vacanza;
    private TimeSlot timeSlot;

    public ScocciaturaVacanza() {
    }

    public ScocciaturaVacanza(int peso, Holiday vacanza, TimeSlot timeSlot) {
        this.peso = peso;
        this.vacanza = vacanza;
        this.timeSlot = timeSlot;
    }

    @Override
    public int calcolaUffa(ContestoScocciatura contesto) {

        TimeSlot timeSlot = contesto.getConcreteShift().getShift().getTimeSlot();
        //LocalDate data = contesto.getConcreteShift().getDate();

       // if(data.equals(this.vacanza.getStartDate()) && timeSlot.equals(this.timeSlot))
       //     return this.peso;

        return 0;
    }
}
