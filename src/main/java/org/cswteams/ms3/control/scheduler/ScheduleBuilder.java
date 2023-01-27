package org.cswteams.ms3.control.scheduler;

import java.time.LocalDate;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.cswteams.ms3.entity.vincoli.ContestoVincolo;
import org.cswteams.ms3.entity.vincoli.Vincolo;
import org.cswteams.ms3.exception.IllegalAssegnazioneTurnoException;
import org.cswteams.ms3.exception.NotEnoughFeasibleUsersException;
import org.cswteams.ms3.exception.UnableToBuildScheduleException;
import org.cswteams.ms3.exception.ViolatedConstraintException;
import org.cswteams.ms3.entity.AssegnazioneTurno;
import org.cswteams.ms3.entity.Schedule;
import org.cswteams.ms3.entity.UserScheduleState;
import org.cswteams.ms3.entity.Utente;

import lombok.Data;

@Data
public class ScheduleBuilder {
    
    private Logger logger = Logger.getLogger(ScheduleBuilder.class.getName());
    
    /** Lista di vincoli da applicare a ogni coppia AssegnazioneTurno, Utente */
    private List<Vincolo> allConstraints;

    /** Oggetti che rappresentano lo stato relativo alla costruzione della pianificazione
     * per ogni utente partecipante
     */
    private Map<Long, UserScheduleState> allUserScheduleStates;

    /** Pianificazione in costruzione */
    private Schedule schedule;


    public ScheduleBuilder(LocalDate startDate, LocalDate endDate, List<Vincolo> allConstraints, List<AssegnazioneTurno> allAssignedShifts, List<Utente> users) {
        this.schedule = new Schedule(startDate, endDate);
        this.schedule.setAssegnazioniTurno(allAssignedShifts);
        this.allConstraints = allConstraints;
        this.allUserScheduleStates = new HashMap<>();
        initializeUserScheduleStates(users);
    }

    public ScheduleBuilder(List<Vincolo> allConstraints, List<Utente> all, Schedule schedule) {
        this.allConstraints = allConstraints;
        this.schedule=schedule;
        this.allUserScheduleStates = new HashMap<>();
        initializeUserScheduleStates(all);

    }

    /** Imposta stato per tutti gli utenti disponibili per la pianificazione */
    private void initializeUserScheduleStates(List<Utente> users){
        
        for (Utente u : users){
            UserScheduleState usstate = new UserScheduleState(u, schedule);
            allUserScheduleStates.put(u.getId(), usstate);
        }        
    }



    /** invoca la creazione automatica della pianificazione 
     * @throws UnableToBuildScheduleException
     * */
    public Schedule build() throws UnableToBuildScheduleException{
        Set<Utente> utentiGuardia;
        for( AssegnazioneTurno at : this.schedule.getAssegnazioniTurno()){
            
            try {
                // Prima pensiamo a riempire la guardia, che è la più importante
                utentiGuardia = this.ricercaUtenti(at, at.getTurno().getNumUtentiGuardia(), null);
                at.setUtentiDiGuardia(utentiGuardia);
                for (Utente u : utentiGuardia){
                    allUserScheduleStates.get(u.getId()).addAssegnazioneTurno(at);
                }
            } catch (NotEnoughFeasibleUsersException e) {
                throw new UnableToBuildScheduleException("unable to select utenti di guardia", e);
            }

            try {
                // Passo poi a riempire la reperibilità
                at.setUtentiReperibili(this.ricercaUtenti(at, at.getTurno().getNumUtentiReperibilita(),utentiGuardia));
            } catch (NotEnoughFeasibleUsersException e) {
                throw new UnableToBuildScheduleException("unable to select utenti di reperibilita", e);
            }
        }

        return this.schedule;
    }

    /** seleziona gli utenti per una lista di utenti assegnati (guardia, reperibilità, ...) per una assegnazione di turno 
     * @throws NotEnoughFeasibleUsersException
     * */
    private Set<Utente> ricercaUtenti(AssegnazioneTurno assegnazione, int numUtenti,  Set<Utente> NotAllowedSet) throws NotEnoughFeasibleUsersException{
        
        List<Utente> selectedUsers = new ArrayList<>();

        //Randomizzo la scelta dell'utente dalla lista di tutti gli utenti
        List<UserScheduleState> allUserScheduleState = new ArrayList<>(allUserScheduleStates.values()) ;
        Collections.shuffle(allUserScheduleState);

        for (UserScheduleState userScheduleState : allUserScheduleState){
            if (selectedUsers.size() == numUtenti){
                break;
            }
            //Se viene passato un set di utenti non ammessi (utenti di guardia) allora li esclude
            if (NotAllowedSet!=null && NotAllowedSet.contains(userScheduleState.getUtente())) {
                continue;
            }
            ContestoVincolo contesto = new ContestoVincolo(userScheduleState,assegnazione);
            // Se l'utente rispetta tutti i vincoli possiamo includerlo nella lista desiderata
            try {
                this.verificaTuttiVincoli(contesto);
                selectedUsers.add(userScheduleState.getUtente());
                userScheduleState.addAssegnazioneTurno(contesto.getAssegnazioneTurno());
            } catch (ViolatedConstraintException e) {
                // logghiamo semplicemente l'evento e ignoriamo l'utente inammissibile
                logger.log(Level.WARNING, e.getMessage(), e);
            }
        }
        
        // potrei aver finito senza aver trovato abbastanza utenti
        if (selectedUsers.size() != numUtenti){
            throw new NotEnoughFeasibleUsersException(numUtenti, selectedUsers.size());
        }
        
        return new HashSet<Utente>(selectedUsers);
    }

    /** Applica tutti i vincoli al contesto specificato e ritorna l'AND tra i risultati
     * di ciascuno di essi
     */
    private void verificaTuttiVincoli(ContestoVincolo contesto) throws ViolatedConstraintException {

        for(Vincolo vincolo : this.allConstraints){
            vincolo.verificaVincolo(contesto);
        }
    }

    /**
     * Questo metodo è invocato nel momento in cui è richiesta l'aggiunta di un assegnazione turno in modo forzato.
     * Il metodo va quindi a verificare i soli vincoli che non possono essere violati.
     * @param contesto
     * @throws ViolatedConstraintException
     */
    private void verificaTuttiVincoliForced(ContestoVincolo contesto) throws ViolatedConstraintException{

        for(Vincolo vincolo : this.allConstraints){
            if(!vincolo.isViolabile())
                vincolo.verificaVincolo(contesto);
        }
    }



    /** Aggiunge un'assegnazione turno manualmente alla pianificazione.
     * L'assegnazione deve già essere compilata con la data e gli utenti.
     * @throws IllegalAssegnazioneTurnoException
     */
    public Schedule addAssegnazioneTurno(AssegnazioneTurno at, boolean forced) throws IllegalAssegnazioneTurnoException{
        
        for (Utente u : at.getUtenti()){

            //verifico se è richiesta un aggiunta di assegnazione turno in modo forzata
            if(!forced){

                //Se non è richiesta la forzatura allora si verificano tutti i vincoli
                try {
                    verificaTuttiVincoli(new ContestoVincolo(this.allUserScheduleStates.get(u.getId()), at));
                    this.allUserScheduleStates.get(u.getId()).addAssegnazioneTurno(at);
                } catch (ViolatedConstraintException e) {
                    throw new IllegalAssegnazioneTurnoException(e);
                }
            }else{

                //Se è richiesta la forzatura si verificano solo i vincoli non violabili
                try {
                    verificaTuttiVincoliForced(new ContestoVincolo(this.allUserScheduleStates.get(u.getId()), at));
                    this.allUserScheduleStates.get(u.getId()).addAssegnazioneTurno(at);
                } catch (ViolatedConstraintException e) {
                    throw new IllegalAssegnazioneTurnoException(e);
                }
            }

        }

        this.schedule.getAssegnazioniTurno().add(at);
        return this.schedule;
    }
}
