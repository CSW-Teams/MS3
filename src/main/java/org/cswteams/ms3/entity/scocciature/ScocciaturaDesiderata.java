package org.cswteams.ms3.entity.scocciature;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.cswteams.ms3.entity.Preference;
import org.cswteams.ms3.enums.TimeSlot;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.time.LocalDate;
import java.util.List;

/**

 * Rappresenta una {@link Scocciatura} che calcola un peso (penalità) per un medico

 * se viene assegnato a un {@link org.cswteams.ms3.entity.ConcreteShift turno concreto} in un giorno

 * o {@link TimeSlot fascia horaria} per i quali ha espresso una {@link Preference preferenza}

 * a non lavorare.

 *

 * Le "scocciature" sono entità persistenti che generano i delta di priorità UFFA.

 * (Microtask 1.2 - Pipeline priorità UFFA/scocciatura).

 *

 * @see docs/AI_powered_rescheduling/sprint_4/story_1.md#microtask-12--vincoli-e-pipeline-priorità-baseline

 */

@Entity

@Getter

@EqualsAndHashCode(callSuper = true)

public class ScocciaturaDesiderata extends Scocciatura {



    /** Il peso (penalità) di questa scocciatura. */
    private int peso;

    /**
     * Default constructor needed by Lombok
     */
    public ScocciaturaDesiderata() {
    }

    public ScocciaturaDesiderata(int peso) {
        this.peso = peso;
    }

    /**
     * Calcola il valore di "uffa" (penalità) per l'assegnazione di un medico,
     * se il {@link org.cswteams.ms3.entity.ConcreteShift turno concreto} nel {@link ContestoScocciatura contesto}
     * corrisponde a una {@link Preference desiderata} del medico a non lavorare in quel giorno o {@link TimeSlot fascia oraria}.
     *
     * @param contesto Il {@link ContestoScocciatura contesto} dell'assegnazione.
     * @return Il peso della scocciatura se le condizioni sono soddisfatte, altrimenti 0.
     */
    @Override
    public int calcolaUffa(ContestoScocciatura contesto) {

        TimeSlot timeSlot = contesto.getConcreteShift().getShift().getTimeSlot();

        List<Preference> desiderate = contesto.getDoctorUffaPriority().getDoctor().getPreferenceList();
        for(Preference preference : desiderate){
            if(preference.getDate().equals(LocalDate.ofEpochDay(contesto.getConcreteShift().getDate())) && preference.getTimeSlots().contains(timeSlot))
                return peso;
        }

        return 0;
    }
}
