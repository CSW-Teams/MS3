package org.cswteams.ms3.entity.scocciature;

import lombok.Data;
import org.cswteams.ms3.entity.Desiderata;
import org.cswteams.ms3.enums.TipologiaTurno;

import javax.persistence.Entity;
import java.time.LocalDate;
import java.util.List;

/**
 * Calcola quanto pesa ad un utente essere messo di turno in un giorno in cui ha espresso di non lavorare
 */
@Data
@Entity
public class ScocciaturaDesiderata extends Scocciatura {

    private int peso;

    public ScocciaturaDesiderata() {
    }

    public ScocciaturaDesiderata(int peso) {
        this.peso = peso;
    }

    @Override
    public int calcolaUffa(ContestoScocciatura contesto) {

        List<Desiderata> desiderate = contesto.getUserScheduleState().getUtente().getDesiderataList();

        for(Desiderata desiderata: desiderate){
            if(desiderata.getData().equals(contesto.getAssegnazioneTurno().getData()))
                return peso;
        }

        return 0;
    }
}
