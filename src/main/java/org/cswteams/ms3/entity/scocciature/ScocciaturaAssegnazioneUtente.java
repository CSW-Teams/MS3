package org.cswteams.ms3.entity.scocciature;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.cswteams.ms3.enums.TipologiaTurno;

import javax.persistence.Entity;
import java.time.DayOfWeek;

/**
 * Calcola quanto pesa ad un utente essere assegnato ad una assegnazione in base al giorno della settimana
 * e in base alla tipologia del turno.
 */


@Entity
@Data
@EqualsAndHashCode(callSuper = true)
public class ScocciaturaAssegnazioneUtente extends Scocciatura {

    private int peso;
    private DayOfWeek giornoSettimana;
    private TipologiaTurno tipologiaTurno;

    public ScocciaturaAssegnazioneUtente() {
    }

    public ScocciaturaAssegnazioneUtente(int peso, DayOfWeek giornoSettimana, TipologiaTurno tipologiaTurno) {
        this.peso = peso;
        this.giornoSettimana = giornoSettimana;
        this.tipologiaTurno = tipologiaTurno;
    }

    @Override
    public int calcolaUffa(ContestoScocciatura contesto) {

        TipologiaTurno tipologiaTurno = contesto.getAssegnazioneTurno().getShift().getTipologiaTurno();
        DayOfWeek giornoSettimana = contesto.getAssegnazioneTurno().getData().getDayOfWeek();

        if(giornoSettimana.equals(this.giornoSettimana) && tipologiaTurno.equals(this.tipologiaTurno))
            return this.peso;

        return 0;
    }
}
