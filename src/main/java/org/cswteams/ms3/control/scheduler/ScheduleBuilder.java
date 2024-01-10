package org.cswteams.ms3.control.scheduler;


import lombok.Getter;
import lombok.Setter;
import org.cswteams.ms3.control.scocciatura.ControllerScocciatura;
import org.cswteams.ms3.control.utils.DoctorAssignmentUtil;
import org.cswteams.ms3.dao.DoctorAssignmentDAO;
import org.cswteams.ms3.dao.DoctorDAO;
import org.cswteams.ms3.entity.*;
import org.cswteams.ms3.entity.constraint.Constraint;
import org.cswteams.ms3.entity.constraint.ContestoVincolo;
import org.cswteams.ms3.enums.ConcreteShiftDoctorStatus;
import org.cswteams.ms3.enums.Seniority;
import org.cswteams.ms3.exception.IllegalAssegnazioneTurnoException;
import org.cswteams.ms3.exception.IllegalScheduleException;
import org.cswteams.ms3.exception.NotEnoughFeasibleUsersException;
import org.cswteams.ms3.exception.ViolatedConstraintException;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

@Getter
@Setter
public class ScheduleBuilder {


    private final Logger logger = Logger.getLogger(ScheduleBuilder.class.getName());

    /** List of constraints to be applied to each couple (ConcreteShift, User) */
    @NotNull
    private List<Constraint> allConstraints;

    /** Objects representing the state of schedule building for each participant doctor */
    private Map<Long, DoctorScheduleState> allUserScheduleStates;

    /** Shift schedule to be built */
    private Schedule schedule;

    /** Instance of controllerScocciatura */
    private ControllerScocciatura controllerScocciatura;

    /**
     * This method validates date parameters passed to the schedule builder.
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
     * This method has the responsibility of checking if the shifts have assigned the doctors which
     * are listed in the available user list.
     * @param allAssignedShifts List of shifts containing the assigned user
     * @param doctors List of doctors which are available for a certain shift
     * @throws IllegalScheduleException An exception highlighting the incoherent state of the passed parameters
     */
    private void validateUsers(List<ConcreteShift> allAssignedShifts, List<Doctor> doctors) throws IllegalScheduleException {
        for (ConcreteShift concreteShift: allAssignedShifts){
            for(DoctorAssignment da : concreteShift.getDoctorAssignmentList()){
                Doctor shiftDoctor = da.getDoctor();
                if(!doctors.contains(shiftDoctor))
                    throw new IllegalScheduleException("[ERROR] Incoherent state between doctors assigned in the concreteShift and doctors listed in the available ones");
            }
        }
    }

    /**
     * This method has the responsibility of checking if the constraints aren't a null object.
     * @param allConstraints Constraints passed as parameters of the schedule builder
     * @throws IllegalScheduleException Exception thrown when there are some problems in the configuration parameters of the schedule
     */
    private void validateConstraints(List<Constraint> allConstraints) throws IllegalScheduleException {
        if(allConstraints == null)
            throw new IllegalScheduleException("[ERROR] Cannot have null constraints");

        for(Constraint constraint: allConstraints)
            if (constraint == null)
                throw new IllegalScheduleException("[ERROR] Cannot have null constraints");
    }

    /**
     * This method has the responsibility to create a new instance of schedule and save it in persistence.
     * @param startDate Date of the start of the new schedule
     * @param endDate Date of the end of the new schedule
     * @param allConstraints Set of constraints to not be violated
     * @param allAssignedShifts Set of all shifts that are already assigned to a set of people
     * @param doctors Set of doctors that can be added in the schedule
     * @throws IllegalScheduleException Exception thrown when there are some problems in the configuration parameters of the schedule
     */
    public ScheduleBuilder(LocalDate startDate, LocalDate endDate, List<Constraint> allConstraints, List<ConcreteShift> allAssignedShifts, List<Doctor> doctors) throws IllegalScheduleException {
        // Checks on the parameters state

        validateDates(startDate,endDate);
        validateUsers(allAssignedShifts, doctors);
        validateConstraints(allConstraints);

        // Actual initialization
        this.schedule = new Schedule(startDate.toEpochDay(), endDate.toEpochDay(), Collections.emptyList());
        this.schedule.setConcreteShifts(allAssignedShifts);
        this.allConstraints = allConstraints;
        this.allUserScheduleStates = new HashMap<>();
        initializeUserScheduleStates(doctors);
    }

    /**
     * This method has the responsibility of checking if the schedule isn't a null object
     * @param schedule Schedule from which we want to generate a new one
     */
    private void validateSchedule(Schedule schedule) throws IllegalScheduleException {
        if(schedule == null)
            throw new IllegalScheduleException("[ERROR] Cannot create new schedule from a null one");
        else if (schedule.getCauseIllegal() == null)
            throw new IllegalScheduleException("[ERROR] Cannot create new schedule from an illegal one");
    }

    /**
     * This method has the responsibility of creating a new valid schedule from an existing one
     * @param allConstraints Set of constraints to not be violated
     * @param doctors Set of doctors that can be added in the schedule
     * @param schedule An existing schedule from which to start a new one
     * @throws IllegalScheduleException Exception thrown when there are some problems in the configuration parameters of the schedule
     */
    public ScheduleBuilder(List<Constraint> allConstraints, List<Doctor> doctors, Schedule schedule) throws IllegalScheduleException {
        // Checks on the parameters state
        validateConstraints(allConstraints);
        validateSchedule(schedule);

        this.allConstraints = allConstraints;
        this.schedule=schedule;
        this.allUserScheduleStates = new HashMap<>();
        initializeUserScheduleStates(doctors);
    }



    /**
     * Private method that has the responsibility of initializing the state of the schedule for all the users.
     * @param doctors Set of doctors that can be added in the schedule
     */
    private void initializeUserScheduleStates(List<Doctor> doctors){

        for (Doctor u : doctors){
            DoctorScheduleState usstate = new DoctorScheduleState(u, schedule);
            allUserScheduleStates.put(u.getId(), usstate);
        }
    }
    public Schedule build(){
        schedule.getViolatedConstraints().clear();
        schedule.setCauseIllegal(null);
        for( ConcreteShift concreteShift : this.schedule.getConcreteShifts()){
            // First step: define doctors on duty in the concrete shift.
            try {
                //TODO:Revisionare questo if
                //Questa linea va rivalutata in seguito
                List<Doctor> doctorsOnDuty = DoctorAssignmentUtil.getDoctorsInConcreteShift(concreteShift, Collections.singletonList(ConcreteShiftDoctorStatus.ON_DUTY));
                int count=0;
                for (Map.Entry<Seniority, Integer>  qss : concreteShift.getShift().getQuantityShiftSeniority().entrySet()){
                    count += qss.getValue();
                    this.addDoctors(concreteShift, qss, doctorsOnDuty,ConcreteShiftDoctorStatus.ON_CALL,count);
                }
                if(concreteShift.getShift().getMedicalService().getTasks().size()>count){
                    throw new NotEnoughFeasibleUsersException(concreteShift.getShift().getMedicalService().getTasks().size(),count);
                }
            } catch (NotEnoughFeasibleUsersException e) {
                // There are not enough doctors on duty available: we define the violation of constraints and stop the schedule generation.
                logger.log(Level.SEVERE, e.getMessage(), e);
                schedule.setCauseIllegal(e);

                logger.log(Level.SEVERE, schedule.getCauseIllegal().toString());
                for (Constraint constraint : schedule.getViolatedConstraints()){
                    logger.log(Level.SEVERE, constraint.toString());
                }

            }

            // Second step: define doctors on call in the concrete shift.
            try {
                List<Doctor> doctorsOnCall = DoctorAssignmentUtil.getDoctorsInConcreteShift(concreteShift, Collections.singletonList(ConcreteShiftDoctorStatus.ON_CALL));
                int count=0;
                for (Map.Entry<Seniority, Integer>  qss : concreteShift.getShift().getQuantityShiftSeniority().entrySet()){
                    count += qss.getValue();
                    this.addDoctors(concreteShift, qss, doctorsOnCall,ConcreteShiftDoctorStatus.ON_DUTY, count);
                }

            } catch (NotEnoughFeasibleUsersException e){
                // Here we define the violation of constraints but do not stop the schedule generation.
                logger.log(Level.SEVERE, e.getMessage(), e);
            }
        }

        return this.schedule;
    }

    /**
     * This method adds some doctors to a list of assigned doctors for a concrete shift.
     * @param concreteShift Concrete shift in which the new doctors have to be assigned
     * @param qss Number of doctors that have to be added to the concrete shift
     * @param doctorList List  of doctor assigned since the last assegnation
     * @param status type of assignation that we want on the concrateshift
     * @param selectedTask index of task that we want to start the assegnation
     * @throws NotEnoughFeasibleUsersException Exception thrown if the number of doctors having the possibility to be
     * added to the concrete shift is less than numDoctors
     */
    private void addDoctors(ConcreteShift concreteShift, Map.Entry<Seniority, Integer> qss, List<Doctor> doctorList,ConcreteShiftDoctorStatus status,int selectedTask) throws NotEnoughFeasibleUsersException{
       int selectedUser=0;
        List<DoctorScheduleState> allDoctorScheduleState = new ArrayList<>(allUserScheduleStates.values());
        if(controllerScocciatura != null){
            controllerScocciatura.addUffaTempUtenti(allDoctorScheduleState,concreteShift);
            controllerScocciatura.ordinaByUffa(allDoctorScheduleState);
        }
       for(DoctorScheduleState d:allDoctorScheduleState){
           if (selectedUser == qss.getValue()){
               break;
           }
           //TODO: aggiungere controllo specializzazione
           if(d.getDoctor().getSeniority()!=qss.getKey())
               continue;
           ContestoVincolo context = new ContestoVincolo(d,concreteShift);
           if(verifyAllConstraints(context, false)){
               doctorList.add(d.getDoctor());
               d.addConcreteShift(context.getConcreteShift());
               //Creo il Doctor Assignement
               int indexTask=selectedTask %(concreteShift.getShift().getMedicalService().getTasks().size());
               DoctorAssignment da = new DoctorAssignment(d.getDoctor(),
                                                            status,
                                                            concreteShift,
                                                        concreteShift.getShift().getMedicalService().getTasks().get(indexTask));
               //lo inserisco nei concrateShift
               concreteShift.getDoctorAssignmentList().add(da);
               selectedTask++;
           }
           List<Doctor> contextDoctorsOnDuty = DoctorAssignmentUtil.getDoctorsInConcreteShift(context.getConcreteShift(), Collections.singletonList(status));
           if(contextDoctorsOnDuty.size() < qss.getValue())
               d.saveUffaTemp();
           selectedUser++;
       }
        // Case in which the algorithm ends without having found enough doctors to place into the concrete shift
        if (selectedUser != qss.getValue()){
            throw new NotEnoughFeasibleUsersException(qss.getValue(), selectedUser);
        }
    }


    /**
     * This method applies all the constraints to the specified context. If a constraint is violated, then it is added
     * to the log. Moreover, if the violated constraint is hard, then the shift schedule is marked as illegal and the
     * cause of the violation is initialized.
     * @param context Context in which all the constraints are applied and verified
     * @param isForced Boolean that represents if it is possible to violate the soft constraints
     * @return True if there are no violations or the only verified violations are soft with isForced==true; false otherwise
     */
    private boolean verifyAllConstraints(ContestoVincolo context, boolean isForced){

        //This flag indicates if there has been a violation in the constraints.
        boolean isOk = true;

        for(Constraint constraint : this.allConstraints){
            try {
                constraint.verificaVincolo(context);
            } catch (ViolatedConstraintException e) {

                //schedule.getViolatedConstraintLog().add(new ViolatedConstraintLogEntry(e));

                // If the violated constraint is hard, then the shift schedule is illegal.
                if (!constraint.isViolable() || (constraint.isViolable() && !isForced)){
                    isOk = false;
                }

            }
        }
        return isOk;

    }

    /**
     * This method add a concrete shift to the schedule manually. The concrete shift shall be already defined with
     * date and doctors.
     * @param concreteShift The concrete shift to be added to the schedule
     * @param isForced Boolean that represents if it is possible to violate the soft constraints with the new concrete shift
     * @return An instance of the updated shift schedule
     */
    public Schedule addConcreteShift(ConcreteShift concreteShift, boolean isForced){
        schedule.getViolatedConstraints().clear();
        schedule.setCauseIllegal(null);

        for (DoctorAssignment da : concreteShift.getDoctorAssignmentList()){
            Doctor doctor = da.getDoctor();
            if (!verifyAllConstraints(new ContestoVincolo(this.allUserScheduleStates.get(doctor.getId()), concreteShift), isForced)){
                schedule.setCauseIllegal(new IllegalAssegnazioneTurnoException("Un vincolo stringente è stato violato, oppure un vincolo non stringente è stato violato e non è stato richiesto di forzare l'assegnazione. Consultare il log delle violazioni della pianificazione può aiutare a investigare la causa."));
            }
        }
        if(schedule.getCauseIllegal() == null){
            for (DoctorAssignment da : concreteShift.getDoctorAssignmentList()){
                Doctor doctor = da.getDoctor();
                this.allUserScheduleStates.get(doctor.getId()).addConcreteShift(concreteShift);
                List<Doctor> doctorsOnDuty = DoctorAssignmentUtil.getDoctorsInConcreteShift(concreteShift, Collections.singletonList(ConcreteShiftDoctorStatus.ON_DUTY));
                if(doctorsOnDuty.contains(doctor))
                    this.allUserScheduleStates.get(doctor.getId()).saveUffaTemp();
            }
            this.schedule.getConcreteShifts().add(concreteShift);
        }

        return this.schedule;

    }
}
