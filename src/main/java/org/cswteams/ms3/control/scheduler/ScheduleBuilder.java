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
import org.cswteams.ms3.entity.ViolatedConstraintLogEntry;

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

        // we need to clear violations and illegal state, if any
        schedule.purify();

        for( AssegnazioneTurno at : this.schedule.getAssegnazioniTurno()){
            
            try {
                
                // Prima pensiamo a riempire le allocazioni, che sono le più importante
                this.aggiungiUtenti(at,at.getTurno().getNumUtentiGuardia(),at.getUtentiDiGuardia());
                for (Utente u : at.getUtentiDiGuardia()){
                    allUserScheduleStates.get(u.getId()).addAssegnazioneTurno(at);
                }

                // Passo poi a riempire le riserve
                this.aggiungiUtenti(at,at.getTurno().getNumUtentiGuardia(),at.getUtentiReperibili());

            } catch (NotEnoughFeasibleUsersException e) {
                
                // non ci sono abbastanza allocati o riserve per questa assegnazione turno, loggiamo l'evento
                // e rendiamo la pianificazione illegale, infine ritorniamo al chiamante
                logger.log(Level.SEVERE, e.getMessage(), e);
                schedule.taint(e);
                return this.schedule;
            }
        }

        return this.schedule;
    }

    /** aggiunge gli utenti per una lista di utenti assegnati per una assegnazione di turno 
     * @throws NotEnoughFeasibleUsersException
     * */
    private void aggiungiUtenti(AssegnazioneTurno assegnazione, int numUtenti,  Set<Utente> utentiDaPopolare) throws NotEnoughFeasibleUsersException{
        
        int selectedUsers = 0;

        //Randomizzo la scelta dell'utente dalla lista di tutti gli utenti
        List<UserScheduleState> allUserScheduleState = new ArrayList<>(allUserScheduleStates.values()) ;
        Collections.shuffle(allUserScheduleState);

        for (UserScheduleState userScheduleState : allUserScheduleState){
            if (selectedUsers == numUtenti){
                break;
            }

            ContestoVincolo contesto = new ContestoVincolo(userScheduleState,assegnazione);
            // Se l'utente rispetta tutti i vincoli possiamo includerlo nella lista desiderata
            // TODO: parametrizzare la costruzione della schedulazione su forzare vincoli stringenti o meno
            if (verificaTuttiVincoli(contesto, false)){
                utentiDaPopolare.add(userScheduleState.getUtente());
                userScheduleState.addAssegnazioneTurno(contesto.getAssegnazioneTurno());
                selectedUsers++;    
            }
        }
        
        // potrei aver finito senza aver trovato abbastanza utenti
        if (selectedUsers != numUtenti){
            throw new NotEnoughFeasibleUsersException(numUtenti, selectedUsers);
        }
        
    }

    /** Applica tutti i vincoli al contesto specificato.
     * Se un vincolo viene violato, viene aggiunto al log delle violazioni della pianificazione.
     * Se il vincolo violato è stringente, lo stato della pianificazione è impostato a illegale
     * e la causa è impostata con la suddetta violazione, infine ritorna al chiamante senza controllare
     * gli altri vincoli.
     * @param contesto
     * @param isForced se dobbiamo forzare i vincoli non stringenti
     * @return True se non sono accadute violazioni oppure le uniche violazione accadute riguardano
     * vincoli non stringenti e si vuole forzarli, false altrimenti
     */
    private boolean verificaTuttiVincoli(ContestoVincolo contesto, boolean isForced){

        for(Vincolo vincolo : this.allConstraints){
            try {
                vincolo.verificaVincolo(contesto);
            } catch (ViolatedConstraintException e) {

                schedule.getViolatedConstraintLog().add(new ViolatedConstraintLogEntry(e));
                
                // se il vincolo violato è stringente, la schedulazione è illegale
                if (!vincolo.isViolabile() || (vincolo.isViolabile() && !isForced)){
                    return false;
                }

            }
        }
        return true;
    }

    /** Aggiunge un'assegnazione turno manualmente alla pianificazione.
     * L'assegnazione deve già essere compilata con la data e gli utenti.
     */
    public Schedule addAssegnazioneTurno(AssegnazioneTurno at, boolean forced){
        
        schedule.purify();
        for (Utente u : at.getUtenti()){

            if (!verificaTuttiVincoli(new ContestoVincolo(this.allUserScheduleStates.get(u.getId()), at), forced)){
                schedule.taint(new IllegalAssegnazioneTurnoException("Un vincolo stringente è stato violato, oppure un vincolo non stringente è stato violato e non è stato richiesto di forzare l'assegnazione. Consultare il log delle violazioni della pianificazione può aiutare a investigare la causa."));

            }
            this.allUserScheduleStates.get(u.getId()).addAssegnazioneTurno(at);
        }
        
        this.schedule.getAssegnazioniTurno().add(at);
        return this.schedule;
    }
}
