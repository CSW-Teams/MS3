package org.cswteams.ms3.entity.vincoli;

import org.cswteams.ms3.entity.RuoloNumero;
import org.cswteams.ms3.entity.doctor.Doctor;
import org.cswteams.ms3.enums.RuoloEnum;
import org.cswteams.ms3.exception.ViolatedConstraintException;
import org.cswteams.ms3.exception.ViolatedVincoloRuoloNumeroException;

import javax.persistence.Entity;
import java.util.HashMap;
import java.util.Set;

/**
 * Vincolo che controlla il numero di utenti per ruolo. Definisce quanti utenti di ogni ruolo devono essere associati ad ogni turno.
 * Ad esempio il turno notturno in reparto deve avere 1 specializzando e uno strutturato allocati e 1 specializzando
 * e uno strutturato in riserva.
 */
@Entity
public class VincoloNumeroDiRuoloTurno extends Vincolo{

    @Override
    public void verificaVincolo(ContestoVincolo contesto) throws ViolatedConstraintException {

        //Verifico se sono stati allocati già tutti gli utenti necessari in guardia
        if (contesto.getAssegnazioneTurno().getUtentiDiGuardia().size() != contesto.getAssegnazioneTurno().getShift().getNumRequiredUsers()) {

            //Verifico se è possibile aggiungere l'utente in guardia
            verifica(contesto,contesto.getAssegnazioneTurno().getUtentiDiGuardia());
        }

        //Verifico se sono stati allocati già tutti gli utenti necessari in reperibilità
        else if(contesto.getAssegnazioneTurno().getUtentiReperibili().size() != contesto.getAssegnazioneTurno().getShift().getNumRequiredUsers()){

            //Verifico se è possibile aggiungere l'utente in reperibilità
            verifica(contesto,contesto.getAssegnazioneTurno().getUtentiReperibili());
        }

        //Entro in questo ramo quando devo aggiungere una nuova assegnazione manualmente
        else{

            //Verifico se in guardia e in reperibilità ho un numero di strutturati e specializzandi sufficienti
            this.contaRuoli(contesto,contesto.getAssegnazioneTurno().getUtentiDiGuardia());
            this.contaRuoli(contesto,contesto.getAssegnazioneTurno().getUtentiReperibili());

        }
    }

    /**
     * Metodo ausiliario per verificare il vincolo e per riciclare codice. Esegue gli stessi controlli sia per gli utenti di
     * guardia che per gli utenti in reperibilità.
     * @param contesto
     * @param utentiAllocati possono essere o gli utenti di guardia o gli utenti in reperibilità gia allocati
     * @throws ViolatedVincoloRuoloNumeroException
     */
    private void verifica(ContestoVincolo contesto,Set<Doctor> utentiAllocati) throws ViolatedVincoloRuoloNumeroException {

        /* Calcola il numero di utenti già allocati con lo stesso ruolo dell'utente
           che si vuole provare ad aggiungere nella schedulazione
         */
        int utentiRuoloAssegnati = 0;
        for (Doctor doctor : utentiAllocati) {
            if (doctor.getRuoloEnum().equals(contesto.getUserScheduleState().getDoctor().getRuoloEnum()))
                utentiRuoloAssegnati++;
        }

        /* Per ogni Ruolo richiesto dall'assegnazione turno */
        for (RuoloNumero ruoloNumero : contesto.getAssegnazioneTurno().getShift().getRuoliNumero()) {
            /* Se il numero richiesto di utenti con quel ruolo è già stato raggiunto allora solleva un'eccezione
               Altrimenti aggiungi l'utente all'assegnazione turno
             */
            if (ruoloNumero.getRuolo().equals(contesto.getUserScheduleState().getDoctor().getRuoloEnum())) {
                if (utentiRuoloAssegnati >= ruoloNumero.getNumero())
                    throw new ViolatedVincoloRuoloNumeroException(contesto.getAssegnazioneTurno(), contesto.getUserScheduleState().getDoctor());
            }
        }
    }

    /**
     * Conta ,in un assegnazione turno, quanti utenti per ogni ruolo ci sono in guardia o in reperibilità.
     * @param contesto
     * @param utentiAllocati può essere la lista degli utenti di guardia o degli utenti di reperibilità.
     * @throws ViolatedVincoloRuoloNumeroException
     */
    private void contaRuoli (ContestoVincolo contesto, Set<Doctor> utentiAllocati) throws ViolatedVincoloRuoloNumeroException {

        // Conto quanti utenti di ogni ruoli ci sono nell'assegnazione turno
        // La mappa contiene le informazioni "ruolo-numero di utenti di quel ruolo" presenti nell'assegnazione
        HashMap<RuoloEnum,Integer> counter = new HashMap<>();

        for (Doctor doctor : utentiAllocati) {
            RuoloEnum ruolo = doctor.getRuoloEnum();

            if(counter.get(ruolo)==null){
                counter.put(ruolo,1);
            }
            else{
                counter.put(ruolo,counter.get(ruolo)+1);
            }
        }

        // verifico se nell'assegnazione ho il numero corretto di ruoli
        for (RuoloNumero ruoloNumero: contesto.getAssegnazioneTurno().getShift().getRuoliNumero()) {
            if (counter.get(ruoloNumero.getRuolo()) != null && counter.get(ruoloNumero.getRuolo()) < ruoloNumero.getNumero())
                throw new ViolatedVincoloRuoloNumeroException(contesto.getAssegnazioneTurno(), ruoloNumero, counter.get(ruoloNumero.getRuolo()));
            if (counter.get(ruoloNumero.getRuolo()) == null)
                throw new ViolatedVincoloRuoloNumeroException(contesto.getAssegnazioneTurno(), ruoloNumero, 0);
        }

    }

}
