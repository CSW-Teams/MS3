package org.cswteams.ms3.control.scheduler;

import lombok.Data;
import org.cswteams.ms3.control.scocciatura.ControllerScocciatura;
import org.cswteams.ms3.entity.*;
import org.cswteams.ms3.entity.doctor.Doctor;
import org.cswteams.ms3.entity.vincoli.ContestoVincolo;
import org.cswteams.ms3.entity.vincoli.Vincolo;
import org.cswteams.ms3.exception.*;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

@Data
public class ScheduleBuilder {
    
    private Logger logger = Logger.getLogger(ScheduleBuilder.class.getName());
    
    /** Lista di vincoli da applicare a ogni coppia AssegnazioneTurno, Utente */
    @NotNull
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
     * @throws IllegalScheduleException An exception highlighting the incoherent state of the passed parameters
     */
    private void validateDates(LocalDate startDate, LocalDate endDate) throws IllegalScheduleException {
        if(startDate.isAfter(endDate) || startDate.isEqual(endDate))
            throw new IllegalScheduleException();
        else if(startDate.isBefore(LocalDate.now()))
            throw new IllegalScheduleException("[ERROR] Cannot create a schedule from a date previous than today!");
    }

    /**
     * This class has the responsibility of checking if the shifts have assigned doctors which
     * are listed in the available user list
     * @param allAssignedShifts List of shifts containing the assigned user
     * @param doctors List of doctors which are available for a certain shift
     * @throws IllegalScheduleException An exception highlighting the incoherent state of the passed parameters
     */
    private void validateUsers(List<AssegnazioneTurno> allAssignedShifts, List<Doctor> doctors) throws IllegalScheduleException {
        for (AssegnazioneTurno shift: allAssignedShifts){
            for(Doctor shiftDoctor : shift.getUtenti()){
                if(!doctors.contains(shiftDoctor))
                    throw new IllegalScheduleException("[ERROR] Inchoerent state between doctors assigned in the shift and doctors listed in the available ones");
            }
        }
    }

    /**
     * This class has the responsibility of checking if the constraints aren't a null object
     * @param allConstraints Constraints passed as parameters of the schedule builder
     * @throws IllegalScheduleException Exception thrown when there are some problems in the configuration parameters of the schedule
     */
    private void validateConstraints(List<Vincolo> allConstraints) throws IllegalScheduleException {
        if(allConstraints == null)
            throw new IllegalScheduleException("[ERROR] Cannot have null constraints");

        for(Vincolo constraint: allConstraints)
            if (constraint == null)
                throw new IllegalScheduleException("[ERROR] Cannot have null constraints");
    }

    /**
     * Class that has the responsibility to create a new instance of schedule and save it in persistence
     * @param startDate Date of the start of the new schedule
     * @param endDate Date of the end of the new schedule
     * @param allConstraints Set of constraints to not be violated
     * @param allAssignedShifts Set of all shifts that are already assigned to a set of people
     * @param doctors Set of doctors that is possible to add in the schedule
     * @throws IllegalScheduleException Exception thrown when there are some problems in the configuration parameters of the schedule
     */
    public ScheduleBuilder(LocalDate startDate,LocalDate endDate, List<Vincolo> allConstraints, List<AssegnazioneTurno> allAssignedShifts, List<Doctor> doctors) throws IllegalScheduleException {
        // Checks on the parameters state
        validateDates(startDate,endDate);
        validateUsers(allAssignedShifts, doctors);
        validateConstraints(allConstraints);

        // Actual initialization
        this.schedule = new Schedule(startDate, endDate);
        this.schedule.setAssegnazioniTurno(allAssignedShifts);
        this.allConstraints = allConstraints;
        this.allUserScheduleStates = new HashMap<>();
        initializeUserScheduleStates(doctors);
    }

    /**
     * This class has the responsibility of checking if the schedule isn't a null object
     * @param schedule Schedule from which we want to generate a new one
     */
    private void validateSchedule(Schedule schedule) throws IllegalScheduleException {
        if(schedule == null)
            throw new IllegalScheduleException("[ERROR] Cannot create new schedule from a null one");
        else if (schedule.isIllegal())
            throw new IllegalScheduleException("[ERROR] Cannot create new schedule from an illegal one");
    }

    /**
     * This class has the responsibility of creating a new valid schedule from an existing one
     * @param allConstraints Set of constraints to not be violated
     * @param doctors Set of doctors that is possible to add in the schedule
     * @param schedule An existing schedule from which to start a new one
     * @throws IllegalScheduleException Exception thrown when there are some problems in the configuration parameters of the schedule
     */
    public ScheduleBuilder(List<Vincolo> allConstraints, List<Doctor> doctors, Schedule schedule) throws IllegalScheduleException {
        // Checks on the parameters state
        validateConstraints(allConstraints);
        validateSchedule(schedule);

        this.allConstraints = allConstraints;
        this.schedule=schedule;
        this.allUserScheduleStates = new HashMap<>();
        initializeUserScheduleStates(doctors);
    }



    /**
     * Inner calls that has the responsibility of initializing the state of the schedule for all user
     * @param doctors Set of doctors that is possible to add in the schedule
     */
    private void initializeUserScheduleStates(List<Doctor> doctors){
        
        for (Doctor u : doctors){
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
                
                for (RuoloNumero rn : at.getShift().getRuoliNumero()){
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
                for (RuoloNumero rn : at.getShift().getRuoliNumero()){
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
    private void aggiungiUtenti(AssegnazioneTurno assegnazione, int numUtenti,  Set<Doctor> utentiDaPopolare) throws NotEnoughFeasibleUsersException{
        
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
                utentiDaPopolare.add(userScheduleState.getDoctor());
                userScheduleState.addAssegnazioneTurno(contesto.getAssegnazioneTurno());

                /*
                 * Se il turno a cui ho associato l'utente ha la reperibilità attiva, oppure ho aggiunto l'utente in servizio
                 * allora devo aggiornare il suo uffa cumulato.
                 */
                if(contesto.getAssegnazioneTurno().getShift().isReperibilitaAttiva() || contesto.getAssegnazioneTurno().getUtentiDiGuardia().size() < contesto.getAssegnazioneTurno().getShift().getNumRequiredUsers())
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
        for (Doctor u : at.getUtenti()){

            if (!verificaTuttiVincoli(new ContestoVincolo(this.allUserScheduleStates.get(u.getId()), at), forced)){
                schedule.taint(new IllegalAssegnazioneTurnoException("Un vincolo stringente è stato violato, oppure un vincolo non stringente è stato violato e non è stato richiesto di forzare l'assegnazione. Consultare il log delle violazioni della pianificazione può aiutare a investigare la causa."));
            }
        }
        if(!schedule.isIllegal()){
            for (Doctor u : at.getUtenti()){
                this.allUserScheduleStates.get(u.getId()).addAssegnazioneTurno(at);

                if(at.getShift().isReperibilitaAttiva() || at.getUtentiDiGuardia().contains(u))
                    this.allUserScheduleStates.get(u.getId()).saveUffaTemp();
            }
            this.schedule.getAssegnazioniTurno().add(at);
        }

        return this.schedule;
    }
}
