package org.cswteams.ms3.control.scocciatura;

import org.cswteams.ms3.entity.ConcreteShift;
import org.cswteams.ms3.entity.DoctorScheduleState;
import org.cswteams.ms3.entity.scocciature.ContestoScocciatura;
import org.cswteams.ms3.entity.scocciature.Scocciatura;

import java.util.Collections;
import java.util.List;

/**
 * Classe che gestisce tutti gli aspetti collegata alla metrica UFFA
 */
public class ControllerScocciatura {

    public List<Scocciatura> scocciature;


    public ControllerScocciatura(List<Scocciatura> scocciature) {
        this.scocciature = scocciature;
    }

    /**
     * Calcola quanto pesa ad un utente essere assegnato ad una assegnazione specifica.
     * Calcolato il peso aggiorna lo stato temporaneo dell'utente incrementando il valore di uffa temporaneo.
     * @param utentiState : Stato dell'utente
     * @param concreteShift : Assegnazione a cui vogliamo assegnarla
     */
    public void addUffaTempUtenti(List<DoctorScheduleState> utentiState, ConcreteShift concreteShift){
        int uffa;
        ContestoScocciatura contestoScocciatura;

        for(DoctorScheduleState doctorScheduleState :utentiState ){
            /*contestoScocciatura = new ContestoScocciatura(doctorScheduleState, concreteShift);
            uffa = this.calcolaUffaComplessivoUtenteAssegnazione(contestoScocciatura);
            doctorScheduleState.addUffaTemp(uffa);*/
        }
    }

    /**
     * Ordina la lista di utenti in base al valore di uffaTemporaneo accumulato
     * @param utenti
     * @return
     */
    public void ordinaByUffa(List<DoctorScheduleState> utenti){

        /*
         * Mescoliamo prima la lista degli utenti e poi ordiniamo in base al valore di UFFA.
         * In questo modo facciamo in modo che l'algoritmo di schedulazione peschi in maniera
         * rendomica gli utenti che hanno lo stesso valore di uffa. Ciò è possibile perchè l'algoritmo di ordinamento
         * è in place.
         */
        Collections.shuffle(utenti);
        utenti.sort((u1, u2) -> u1.getUffaParziale() - u2.getUffaParziale());

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
