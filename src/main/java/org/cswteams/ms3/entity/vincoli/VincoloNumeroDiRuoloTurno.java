package org.cswteams.ms3.entity.vincoli;

import org.cswteams.ms3.entity.RuoloNumero;
import org.cswteams.ms3.entity.Utente;
import org.cswteams.ms3.exception.ViolatedConstraintException;

import javax.persistence.Entity;
import java.util.List;

@Entity
public class VincoloNumeroDiRuoloTurno extends Vincolo{

    @Override
    public void verificaVincolo(ContestoVincolo contesto) throws ViolatedConstraintException {
        /* TODO: Per ora il vincolo si applica solo agli utenti di guardia */
        if (contesto.getAssegnazioneTurno().getUtentiDiGuardia().size() == contesto.getAssegnazioneTurno().getTurno().getNumUtentiGuardia())
            return;

        /* Calcola il numero di utenti già allocati con lo stesso ruolo dell'utente
           che si vuole provare ad aggiungere nella schedulazione
         */
        int utentiRuoloAssegnati =0;
        for (Utente utente : contesto.getAssegnazioneTurno().getUtentiDiGuardia()) {
            if (utente.getRuoloEnum().equals(contesto.getUserScheduleState().getUtente().getRuoloEnum()))
                utentiRuoloAssegnati++;
        }

        /* Per ogni Ruolo richiesto dall'assegnazione turno */
        for (RuoloNumero ruoloNumero : contesto.getAssegnazioneTurno().getTurno().getRuoliNumero()) {
            /* Se il numero richiesto di utenti con quel ruolo è già stato raggiunto allora solleva un'eccezione
               Altrimenti aggiungi l'utente all'assegnazione turno
             */
            if (ruoloNumero.getRuolo().equals(contesto.getUserScheduleState().getUtente().getRuoloEnum())) {
                if (utentiRuoloAssegnati >= ruoloNumero.getNumero())
                    throw new ViolatedConstraintException();
                else
                    contesto.getAssegnazioneTurno().addUtentediGuardia(contesto.getUserScheduleState().getUtente());
            }
        }

    }

}
