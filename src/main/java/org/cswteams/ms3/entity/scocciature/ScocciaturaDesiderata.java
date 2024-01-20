package org.cswteams.ms3.entity.scocciature;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.cswteams.ms3.entity.Preference;
import org.cswteams.ms3.enums.TimeSlot;

import javax.persistence.Entity;
import java.time.LocalDate;
import java.util.List;

/**
 * Calcola quanto pesa ad un utente essere messo di turno in un giorno in cui ha espresso di non lavorare
 */

@Entity
@Getter
@EqualsAndHashCode(callSuper = true)
public class ScocciaturaDesiderata extends Scocciatura {

    private int peso;

    public ScocciaturaDesiderata() {
    }

    public ScocciaturaDesiderata(int peso) {
        this.peso = peso;
    }

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
