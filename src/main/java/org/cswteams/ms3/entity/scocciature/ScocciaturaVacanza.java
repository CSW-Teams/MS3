package org.cswteams.ms3.entity.scocciature;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.cswteams.ms3.entity.Holiday;
import org.cswteams.ms3.enums.TipologiaTurno;

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
    private TipologiaTurno tipologiaTurno;

    public ScocciaturaVacanza() {
    }

    public ScocciaturaVacanza(int peso, Holiday vacanza, TipologiaTurno tipologiaTurno) {
        this.peso = peso;
        this.vacanza = vacanza;
        this.tipologiaTurno = tipologiaTurno;
    }

    @Override
    public int calcolaUffa(ContestoScocciatura contesto) {

        TipologiaTurno tipologiaTurno = contesto.getAssegnazioneTurno().getShift().getTipologiaTurno();
        LocalDate data = contesto.getAssegnazioneTurno().getData();

        if(data.equals(this.vacanza.getStartDate()) && tipologiaTurno.equals(this.tipologiaTurno))
            return this.peso;

        return 0;
    }
}
