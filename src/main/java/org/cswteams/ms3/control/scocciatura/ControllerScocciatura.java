package org.cswteams.ms3.control.scocciatura;

import lombok.Data;
import org.cswteams.ms3.entity.AssegnazioneTurno;
import org.cswteams.ms3.entity.UserScheduleState;
import org.cswteams.ms3.entity.scocciature.ContestoScocciatura;
import org.cswteams.ms3.entity.scocciature.Scocciatura;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Classe che gestisce tutti gli aspetti collegata alla metrica UFFA
 */
@Data
public class ControllerScocciatura {

    public List<Scocciatura> scocciature;


    public ControllerScocciatura(List<Scocciatura> scocciature) {
        this.scocciature = scocciature;
    }

    /**
     * Calcola quanto pesa ad un utente essere assegnato ad una assegnazione specifica.
     * Calcolato il peso aggiorna lo stato temporaneo dell'utente incrementando il valore di uffa temporaneo.
     * @param utentiState : Stato dell'utente
     * @param assegnazioneTurno : Assegnazione a cui vogliamo assegnarla
     */
    public void addUffaTempUtenti(List<UserScheduleState> utentiState, AssegnazioneTurno assegnazioneTurno){
        int uffa;
        ContestoScocciatura contestoScocciatura;

        for(UserScheduleState userScheduleState:utentiState ){
            contestoScocciatura = new ContestoScocciatura(userScheduleState,assegnazioneTurno);
            uffa = this.calcolaUffaComplessivoUtenteAssegnazione(contestoScocciatura);
            userScheduleState.addUffaTemp(uffa);
        }
    }

    /**
     * Ordina la lista di utenti in base al valore di uffaTemporaneo accumulato
     * @param utenti
     * @return
     */
    public void ordinaByUffa(List<UserScheduleState> utenti){

        utenti.sort((u1, u2) -> u1.getUffaParziale() - u2.getUffaParziale());
        return;
    }

    /**
     * Calcola quanto pesa ad un utente essere associato ad una assegnazione turno specifica considerando tutte
     * le scocciature
     * @param contestoScocciatura
     * @return
     */
    public int calcolaUffaComplessivoUtenteAssegnazione(ContestoScocciatura contestoScocciatura){
        int uffa = 0;

        for(Scocciatura scocciatura: scocciature){
            uffa += scocciatura.calcolaUffa(contestoScocciatura);
        }

        return uffa;
    }

}
