package org.cswteams.ms3.control.scheduler;

import java.time.LocalDate;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.cswteams.ms3.control.scocciatura.ControllerScocciatura;
import org.cswteams.ms3.entity.vincoli.ContestoVincolo;
import org.cswteams.ms3.entity.vincoli.Vincolo;
import org.cswteams.ms3.exception.*;
import org.cswteams.ms3.entity.AssegnazioneTurno;
import org.cswteams.ms3.entity.RuoloNumero;
import org.cswteams.ms3.entity.Schedule;
import org.cswteams.ms3.entity.UserScheduleState;
import org.cswteams.ms3.entity.Utente;
import org.cswteams.ms3.entity.ViolatedConstraintLogEntry;

import lombok.Data;

import javax.validation.constraints.NotNull;

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

    private ControllerScocciatura controllerScocciatura;

    /**
     * Method to validate dates parameters passed to the schedule builder
     * @param startDate Date of the start of the schedule
     * @param endDate Date of the end of the schedule
     */
    private void validateDates(LocalDate startDate, LocalDate endDate) throws IllegalScheduleException {
        if(startDate.isAfter(endDate) || startDate.isEqual(endDate))
            throw new IllegalScheduleException();
        else if(startDate.isBefore(LocalDate.now()))
            throw new IllegalScheduleException("[ERROR] Cannot create a schedule from a date previous than today!");
    }


    /**
     * Class that has the responsibility to create a new instance of schedule and save it in persistence
     * @param startDate Date of the start of the new schedule
     * @param endDate Date of the end of the new schedule
     * @param allConstraints Set of constraints to not be violated
     * @param allAssignedShifts Set of all shifts that are already assigned to a set of people
     * @param users Set of users that is possible to add in the schedule
     * @throws IllegalScheduleException Exception thrown when there are some problems in the configuration parameters of the schedule
     */
    public ScheduleBuilder(@NotNull LocalDate startDate, @NotNull LocalDate endDate, @NotNull  List<Vincolo> allConstraints, List<AssegnazioneTurno> allAssignedShifts, List<Utente> users) throws IllegalScheduleException {
        validateDates(startDate,endDate);
        this.schedule = new Schedule(startDate, endDate);
        this.schedule.setAssegnazioniTurno(allAssignedShifts);
        this.allConstraints = allConstraints;
        this.allUserScheduleStates = new HashMap<>();
        initializeUserScheduleStates(users);
    }


    /**
     * This class has the responsibility of creating a new valid schedule from an existing one
     * @param allConstraints Set of constraints to not be violated
     * @param users Set of users that is possible to add in the schedule
     * @param schedule An existing schedule from which to start a new one
     */
    public ScheduleBuilder(@NotNull List<Vincolo> allConstraints, List<Utente> users, @NotNull Schedule schedule) {
        this.allConstraints = allConstraints;
        this.schedule=schedule;
        this.allUserScheduleStates = new HashMap<>();
        initializeUserScheduleStates(users);
    }

    /**
     * Inner calls that has the responsibility of initializing the state of the schedule for all user
     * @param users Set of users that is possible to add in the schedule
     */
    private void initializeUserScheduleStates(List<Utente> users){
        
        for (Utente u : users){
            UserScheduleState usstate = new UserScheduleState(u, schedule);
            allUserScheduleStates.put(u.getId(), usstate);
        }        
    }

    /** invoca la creazione automatica della pianificazione 
     * @throws UnableToBuildScheduleException
     * */
    public Schedule build(){

        // we need to clear violations and illegal state, if any
        schedule.purify();

        for( AssegnazioneTurno at : this.schedule.getAssegnazioniTurno()){
            
            try {
                
                // Prima pensiamo a riempire le allocazioni, che sono le più importante
                
                for (RuoloNumero rn : at.getTurno().getRuoliNumero()){
                    this.aggiungiUtenti(at, rn.getNumero(), at.getUtentiDiGuardia());
                }
            } catch (NotEnoughFeasibleUsersException e) {
                
                // non ci sono abbastanza allocati o riserve per questa assegnazione turno, loggiamo l'evento
                // e rendiamo la pianificazione illegale, infine ritorniamo al chiamante
                logger.log(Level.SEVERE, e.getMessage(), e);
                schedule.taint(e);

                logger.log(Level.SEVERE, schedule.getCauseIllegal().toString());
                for (ViolatedConstraintLogEntry vclEntry : schedule.getViolatedConstraintLog()){
                    logger.log(Level.SEVERE, vclEntry.toString());
                }

            }
                
            // Passo poi a riempire le riserve
            try {
                for (RuoloNumero rn : at.getTurno().getRuoliNumero()){
                    this.aggiungiUtenti(at, rn.getNumero(), at.getUtentiReperibili());
                }
            } catch (NotEnoughFeasibleUsersException e){
                // loggiamo l'evento, tuttavia non interrompiamo la pianificazione
                logger.log(Level.SEVERE, e.getMessage(), e);
            }
        }

        return this.schedule;
    }

    /** aggiunge gli utenti per una lista di utenti assegnati per una assegnazione di turno 
     * @throws NotEnoughFeasibleUsersException
     * */
    private void aggiungiUtenti(AssegnazioneTurno assegnazione, int numUtenti,  Set<Utente> utentiDaPopolare) throws NotEnoughFeasibleUsersException{
        
        int selectedUsers = 0;


        List<UserScheduleState> allUserScheduleState = new ArrayList<>(allUserScheduleStates.values()) ;

        //Se il controller della scocciatura è settato ordino gli utenti in base al valore di uffa
        if(controllerScocciatura != null){
            controllerScocciatura.addUffaTempUtenti(allUserScheduleState,assegnazione);
            controllerScocciatura.ordinaByUffa(allUserScheduleState);
        }

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

                /*
                 * Se il turno a cui ho associato l'utente ha la reperibilità attiva, oppure ho aggiunto l'utente in servizio
                 * allora devo aggiornare il suo uffa cumulato.
                 */
                if(contesto.getAssegnazioneTurno().getTurno().isReperibilitaAttiva() || contesto.getAssegnazioneTurno().getUtentiDiGuardia().size() < contesto.getAssegnazioneTurno().getTurno().getNumRequiredUsers())
                    userScheduleState.saveUffaTemp();

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
     * e la causa è impostata con la suddetta violazione.
     * @param contesto
     * @param isForced se dobbiamo forzare i vincoli non stringenti
     * @return True se non sono accadute violazioni oppure le uniche violazione accadute riguardano
     * vincoli non stringenti e si vuole forzarli, false altrimenti
     */
    private boolean verificaTuttiVincoli(ContestoVincolo contesto, boolean isForced){

        /** Questa flag ci comunica se è stata riscontrata una violazione dei vincoli */
        boolean isOk = true;
        
        for(Vincolo vincolo : this.allConstraints){
            try {
                vincolo.verificaVincolo(contesto);
            } catch (ViolatedConstraintException e) {

                schedule.getViolatedConstraintLog().add(new ViolatedConstraintLogEntry(e));
                
                // se il vincolo violato è stringente, la schedulazione è illegale.
                // Inoltre, segnaliamo che almeno un vincolo è stato violato
                if (!vincolo.isViolabile() || (vincolo.isViolabile() && !isForced)){
                    isOk = false;
                }

            }
        }
        return isOk;
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
        }
        if(!schedule.isIllegal()){
            for (Utente u : at.getUtenti()){
                this.allUserScheduleStates.get(u.getId()).addAssegnazioneTurno(at);

                if(at.getTurno().isReperibilitaAttiva() || at.getUtentiDiGuardia().contains(u))
                    this.allUserScheduleStates.get(u.getId()).saveUffaTemp();
            }
            this.schedule.getAssegnazioniTurno().add(at);
        }

        return this.schedule;
    }
}
