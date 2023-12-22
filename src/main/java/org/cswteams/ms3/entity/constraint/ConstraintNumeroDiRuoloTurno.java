package org.cswteams.ms3.entity.constraint;

import org.cswteams.ms3.entity.Doctor;
import org.cswteams.ms3.exception.ViolatedConstraintException;
import org.cswteams.ms3.exception.ViolatedVincoloRuoloNumeroException;

import javax.persistence.Entity;
import java.util.Set;

/**
 * Constraint che controlla il numero di utenti per ruolo. Definisce quanti utenti di ogni ruolo devono essere associati ad ogni turno.
 * Ad esempio il turno notturno in reparto deve avere 1 specializzando e uno strutturato allocati e 1 specializzando
 * e uno strutturato in riserva.
 */
@Entity
public class ConstraintNumeroDiRuoloTurno extends Constraint {

    @Override
    public void verificaVincolo(ContestoVincolo contesto) throws ViolatedConstraintException {
                /*
        TODO: Refactor using new format for doctors on duty
        //Verifico se sono stati allocati già tutti gli utenti necessari in guardia
        if (contesto.getConcreteShift().getDoctorsOnDuty().size() != contesto.getConcreteShift().getShift().getNumRequiredDoctors()) {

            //Verifico se è possibile aggiungere l'utente in guardia
            verifica(contesto,contesto.getConcreteShift().getDoctorsOnDuty());
        }

        //Verifico se sono stati allocati già tutti gli utenti necessari in reperibilità
        else if(contesto.getConcreteShift().getDoctorsOnCall().size() != contesto.getConcreteShift().getShift().getNumRequiredDoctors()){

            //Verifico se è possibile aggiungere l'utente in reperibilità
            verifica(contesto,contesto.getConcreteShift().getDoctorsOnCall());
        }

        //Entro in questo ramo quando devo aggiungere una nuova assegnazione manualmente
        else{

            //Verifico se in guardia e in reperibilità ho un numero di strutturati e specializzandi sufficienti
            this.contaRuoli(contesto,contesto.getConcreteShift().getDoctorsOnDuty());
            this.contaRuoli(contesto,contesto.getConcreteShift().getDoctorsOnCall());

        }

                 */
    }

    /**
     * Metodo ausiliario per verificare il vincolo e per riciclare codice. Esegue gli stessi controlli sia per gli utenti di
     * guardia che per gli utenti in reperibilità.
     * @param contesto
     * @param utentiAllocati possono essere o gli utenti di guardia o gli utenti in reperibilità gia allocati
     * @throws ViolatedVincoloRuoloNumeroException
     */
    private void verifica(ContestoVincolo contesto,Set<Doctor> utentiAllocati) throws ViolatedVincoloRuoloNumeroException {

                        /*
        TODO: Refactor using new "Role" format
        // Calcola il numero di utenti già allocati con lo stesso ruolo dell'utente
        //   che si vuole provare ad aggiungere nella schedulazione

        int utentiRuoloAssegnati = 0;
        for (Doctor doctor : utentiAllocati) {
            if (doctor.getRole().equals(contesto.getDoctorScheduleState().getDoctor().getRole()))
                utentiRuoloAssegnati++;
        }

        // Per ogni Ruolo richiesto dall'assegnazione turno
        for (QuantityShiftSeniority quantityShiftSeniority : contesto.getConcreteShift().getShift().getQuantityShiftSeniority()) {
            // Se il numero richiesto di utenti con quel ruolo è già stato raggiunto allora solleva un'eccezione
            //   Altrimenti aggiungi l'utente all'assegnazione turno

            if (quantityShiftSeniority.getRuolo().equals(contesto.getDoctorScheduleState().getDoctor().getRole())) {
                if (utentiRuoloAssegnati >= quantityShiftSeniority.getNumero())
                    throw new ViolatedVincoloRuoloNumeroException(contesto.getConcreteShift(), contesto.getDoctorScheduleState().getDoctor());
            }
        }
        */
    }

    /**
     * Conta ,in un assegnazione turno, quanti utenti per ogni ruolo ci sono in guardia o in reperibilità.
     * @param contesto
     * @param utentiAllocati può essere la lista degli utenti di guardia o degli utenti di reperibilità.
     * @throws ViolatedVincoloRuoloNumeroException
     */
    private void contaRuoli (ContestoVincolo contesto, Set<Doctor> utentiAllocati) throws ViolatedVincoloRuoloNumeroException {
                /*
        TODO: Refactor using new "Role" format
        // Conto quanti utenti di ogni ruoli ci sono nell'assegnazione turno
        // La mappa contiene le informazioni "ruolo-numero di utenti di quel ruolo" presenti nell'assegnazione
        HashMap<Seniority,Integer> counter = new HashMap<>();

        for (Doctor doctor : utentiAllocati) {
            Seniority ruolo = doctor.getRole();

            if(counter.get(ruolo)==null){
                counter.put(ruolo,1);
            }
            else{
                counter.put(ruolo,counter.get(ruolo)+1);
            }
        }

        // verifico se nell'assegnazione ho il numero corretto di ruoli
        for (QuantityShiftSeniority quantityShiftSeniority : contesto.getConcreteShift().getShift().getQuantityShiftSeniority()) {
            if (counter.get(quantityShiftSeniority.getRuolo()) != null && counter.get(quantityShiftSeniority.getRuolo()) < quantityShiftSeniority.getNumero())
                throw new ViolatedVincoloRuoloNumeroException(contesto.getConcreteShift(), quantityShiftSeniority, counter.get(quantityShiftSeniority.getRuolo()));
            if (counter.get(quantityShiftSeniority.getRuolo()) == null)
                throw new ViolatedVincoloRuoloNumeroException(contesto.getConcreteShift(), quantityShiftSeniority, 0);
        }
*/
    }

}
