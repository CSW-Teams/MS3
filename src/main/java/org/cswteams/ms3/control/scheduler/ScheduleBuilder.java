package org.cswteams.ms3.control.scheduler;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.cswteams.ms3.control.vincoli.ContestoVincolo;
import org.cswteams.ms3.control.vincoli.Vincolo;
import org.cswteams.ms3.control.vincoli.ViolatedConstraintException;
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

    /** Oggetti che raprresentano lo stato relativo alla costruzione della pianificazione
     * per ogni utente partecipante
     */
    private List<UserScheduleState> allUserScheduleStates;

    /** Pianificazione in costruzione */
    private Schedule schedule;


    public ScheduleBuilder(LocalDate startDate, LocalDate endDate, List<Vincolo> allConstraints, List<AssegnazioneTurno> allAssignedShifts, List<Utente> users) {
        this.schedule = new Schedule(startDate, endDate);
        this.schedule.setAssegnazioniTurno(allAssignedShifts);
        this.allConstraints = allConstraints;
        this.allUserScheduleStates = new ArrayList<>();
        initializeUserScheduleStates(users);
    }

    /** Imposta stato per tutti gli utenti disponibili per la pianificazione */
    private void initializeUserScheduleStates(List<Utente> users){
        
        for (Utente u : users){
            UserScheduleState usstate = new UserScheduleState(u, schedule);
            allUserScheduleStates.add(usstate);
        }        
    }

    /** invoca la creazione automatica della pianificazione 
     * @throws UnableToBuildScheduleException
     * */
    public Schedule build() throws UnableToBuildScheduleException{
        Set<Utente> utentiGuardia;
        for( AssegnazioneTurno at : this.schedule.getAssegnazioniTurno()){
            
            try {
                // Prima pensiamo a riempire la guardia, che ?? la pi?? importante
                utentiGuardia = this.ricercaUtenti(at, at.getTurno().getNumUtentiGuardia(), null);
                at.setUtentiDiGuardia(utentiGuardia);
            } catch (NotEnoughFeasibleUsersException e) {
                throw new UnableToBuildScheduleException("unable to select utenti di guardia", e);
            }

            try {
                // Passo poi a riempire la reperibilit??
                at.setUtentiReperibili(this.ricercaUtenti(at, at.getTurno().getNumUtentiReperibilita(),utentiGuardia));
            } catch (NotEnoughFeasibleUsersException e) {
                throw new UnableToBuildScheduleException("unable to select utenti di reperibilita", e);
            }
        }

        return this.schedule;
    }

    /** seleziona gli utenti per una lista di utenti assegnati (guardia, reperibilit??, ...) per una assegnazione di turno 
     * @throws NotEnoughFeasibleUsersException
     * */
    private Set<Utente> ricercaUtenti(AssegnazioneTurno assegnazione, int numUtenti,  Set<Utente> NotAllowedSet) throws NotEnoughFeasibleUsersException{
        
        List<Utente> selectedUsers = new ArrayList<>();

        for (int i = 0; i < allUserScheduleStates.size() && selectedUsers.size() < numUtenti; i ++){
            //Se viene passato un set di utenti non ammessi (utenti di guardia) allora li esclude
            if (NotAllowedSet!=null && NotAllowedSet.contains(allUserScheduleStates.get(i).getUtente())) {
                continue;
            }
            ContestoVincolo contesto = new ContestoVincolo(allUserScheduleStates.get(i).getUtente(),assegnazione);
            // Se l'utente rispetta tutti i vincoli possiamo includerlo nella lista desiderata
            try {
                this.verificaTuttiVincoli(contesto);
                selectedUsers.add(contesto.getUtente());
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
    private void verificaTuttiVincoli(ContestoVincolo contesto) throws ViolatedConstraintException{

        for(Vincolo vincolo : this.allConstraints){
            vincolo.verificaVincolo(contesto);
        }
    }

    /**Aggiunge una assegnazione turno manualmente alla pianificazione, senza
     * apportare nessun tipo di controllo.
     */
    public ScheduleBuilder addAssegnazioneTurnoForced(AssegnazioneTurno at){
        this.schedule.getAssegnazioniTurno().add(at);
        return this;
    }

    /** Aggiunge un'assegnazione turno manualmente alla pianificazione.
     * L'assegnazione deve gi?? essere compilata con la data e gli utenti.
     * @throws IllegalAssegnazioneTurnoException
     */
    public ScheduleBuilder addAssegnazioneTurno(AssegnazioneTurno at) throws IllegalAssegnazioneTurnoException{
        
        for (Utente u : at.getUtenti()){
            
            try {
                verificaTuttiVincoli(new ContestoVincolo(u, at));
            } catch (ViolatedConstraintException e) {
                throw new IllegalAssegnazioneTurnoException(e);
            }
        }
        return addAssegnazioneTurnoForced(at);
    }
}
