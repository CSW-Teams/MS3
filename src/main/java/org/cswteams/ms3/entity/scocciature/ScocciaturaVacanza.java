package org.cswteams.ms3.entity.scocciature;

import lombok.Data;
import org.cswteams.ms3.entity.Holiday;
import org.cswteams.ms3.enums.TipologiaTurno;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.time.DayOfWeek;
import java.time.LocalDate;

/**
 * Calcola quanto pesa ad un utente essere messo di turno in una vacanza
 */
@Data
@Entity
public class ScocciaturaVacanza extends Scocciatura {

    private int peso;
    private LocalDate vacanza;
    private TipologiaTurno tipologiaTurno;

    public ScocciaturaVacanza() {
    }

    public ScocciaturaVacanza(int peso, LocalDate vacanza, TipologiaTurno tipologiaTurno) {
        this.peso = peso;
        this.vacanza = vacanza;
        this.tipologiaTurno = tipologiaTurno;
    }

    @Override
    public int calcolaUffa(ContestoScocciatura contesto) {

        TipologiaTurno tipologiaTurno = contesto.getAssegnazioneTurno().getTurno().getTipologiaTurno();
        LocalDate data = contesto.getAssegnazioneTurno().getData();

        if(data.equals(this.vacanza) && tipologiaTurno.equals(this.tipologiaTurno))
            return this.peso;

        return 0;
    }
}
