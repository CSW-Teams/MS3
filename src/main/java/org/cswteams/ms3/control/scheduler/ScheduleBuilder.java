package org.cswteams.ms3.control.scheduler;


import lombok.Getter;
import lombok.Setter;
import org.cswteams.ms3.control.scocciatura.ControllerScocciatura;
import org.cswteams.ms3.control.utils.DoctorAssignmentUtil;
import org.cswteams.ms3.entity.*;
import org.cswteams.ms3.entity.constraint.Constraint;
import org.cswteams.ms3.entity.constraint.ContextConstraint;
import org.cswteams.ms3.enums.ConcreteShiftDoctorStatus;
import org.cswteams.ms3.enums.PriorityQueueEnum;
import org.cswteams.ms3.enums.Seniority;
import org.cswteams.ms3.enums.TimeSlot;
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
    private Map<Long, DoctorUffaPriority> allDoctorUffaPriorities;

    /** Shift schedule to be built */
    private Schedule schedule;

    /** All the holidays saved in the system */
    private List<Holiday> holidays;

    /** All the associations between doctors and holidays */
    private List<DoctorHolidays> doctorHolidaysList;

    /** All the information about priority levels on the queues of the doctors */
    private List<DoctorUffaPriority> allDoctorUffaPriority;

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
    public ScheduleBuilder(LocalDate startDate, LocalDate endDate, List<Constraint> allConstraints, List<ConcreteShift> allAssignedShifts, List<Doctor> doctors,
                           List<Holiday> holidays, List<DoctorHolidays> doctorHolidaysList, List<DoctorUffaPriority> allDoctorUffaPriority) throws IllegalScheduleException {

        // Checks on the parameters state
        validateDates(startDate,endDate);
        validateUsers(allAssignedShifts, doctors);
        validateConstraints(allConstraints);

        // Actual initialization
        this.schedule = new Schedule(startDate.toEpochDay(), endDate.toEpochDay(), Collections.emptyList());
        this.schedule.setConcreteShifts(allAssignedShifts);
        this.allConstraints = allConstraints;
        this.allDoctorUffaPriorities = new HashMap<>();
        initializeUserScheduleStates(doctors);

        this.holidays = holidays;
        this.doctorHolidaysList = doctorHolidaysList;
        this.allDoctorUffaPriority = allDoctorUffaPriority;

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
        this.allDoctorUffaPriorities = new HashMap<>();
        initializeUserScheduleStates(doctors);
    }



    /**
     * Private method that has the responsibility of initializing the state of the schedule for all the users.
     * @param doctors Set of doctors that can be added in the schedule
     */
    private void initializeUserScheduleStates(List<Doctor> doctors){

        for (Doctor u : doctors){
            DoctorUffaPriority usstate = new DoctorUffaPriority(u, schedule);
            allDoctorUffaPriorities.put(u.getId(), usstate);
        }
    }
    public Schedule build(){
        schedule.getViolatedConstraints().clear();
        schedule.setCauseIllegal(null);

        if(controllerScocciatura != null)   //if controllerScocciatura is instantiated, then we can normalize all the priorities.
            controllerScocciatura.normalizeUffaPriority(allDoctorUffaPriority);

        for( ConcreteShift concreteShift : this.schedule.getConcreteShifts()){
            // First step: define doctors on duty in the concrete shift.
            try {
                //TODO:Revisionare questo if
                //Questa linea va rivalutata in seguito
                List<Doctor> doctorsOnDuty = DoctorAssignmentUtil.getDoctorsInConcreteShift(concreteShift, Collections.singletonList(ConcreteShiftDoctorStatus.ON_DUTY));
                int count=0;
                for (Map.Entry<Seniority, Integer>  qss : concreteShift.getShift().getQuantityShiftSeniority().entrySet()){
                    count += qss.getValue();
                    this.addDoctors(concreteShift, qss, doctorsOnDuty, ConcreteShiftDoctorStatus.ON_CALL, count);
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

        this.schedule.setDoctorUffaPriorityList(allDoctorUffaPriority); //set of all the DoctorUffaPriority instances so that they can be saved in persistence
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
    private void addDoctors(ConcreteShift concreteShift, Map.Entry<Seniority, Integer> qss, List<Doctor> doctorList, ConcreteShiftDoctorStatus status, int selectedTask) throws NotEnoughFeasibleUsersException{

        int selectedUsers=0;
        List<DoctorUffaPriority> allDoctorUffaPriority = new ArrayList<>(allDoctorUffaPriorities.values());

        /* If the concrete shift we are populating is in the afternoon, we get the eventual concrete shift allocated in the morning of the same day.
         * It is important to check if there is someone who can be allocated in a long shift (shift in the morning + shift in the afternoon of the same day).
         */
        ConcreteShift prevConcreteShift = null;
        List<DoctorUffaPriority> prevConcreteShiftDup = null;
        if(concreteShift.getShift().getTimeSlot() == TimeSlot.AFTERNOON) {
            prevConcreteShift = this.getConcreteShift(concreteShift.getDate(), TimeSlot.MORNING);
            if(prevConcreteShift != null)   //case in which there exists a concrete shift the same day in the morning
                prevConcreteShiftDup = this.getDupFromConcreteShift(prevConcreteShift, allDoctorUffaPriority);

        }

        if(controllerScocciatura != null) {
            //general queue has always to be updated
            controllerScocciatura.updatePriorityDoctors(allDoctorUffaPriority, concreteShift, PriorityQueueEnum.GENERAL);
            controllerScocciatura.orderByPriority(allDoctorUffaPriority, PriorityQueueEnum.GENERAL);

            //long shift queue has to be updated only for the doctors that have the possibility to work for a long shift (morning+afternoon)
            if(prevConcreteShift != null) {
                controllerScocciatura.updatePriorityDoctors(prevConcreteShiftDup, concreteShift, PriorityQueueEnum.LONG_SHIFT);
                controllerScocciatura.orderByPriority(allDoctorUffaPriority, PriorityQueueEnum.LONG_SHIFT);

            }

            //night queue has to be updated only if the current concrete shift is nocturne
            if(concreteShift.getShift().getTimeSlot() == TimeSlot.NIGHT) {
                controllerScocciatura.updatePriorityDoctors(allDoctorUffaPriority, concreteShift, PriorityQueueEnum.NIGHT);
                controllerScocciatura.orderByPriority(allDoctorUffaPriority, PriorityQueueEnum.NIGHT);

            }

        }

        for(DoctorUffaPriority dup: allDoctorUffaPriority){
            if (selectedUsers == qss.getValue()){
                break;
            }
            //TODO: aggiungere controllo specializzazione
            if(dup.getDoctor().getSeniority()!=qss.getKey())
                continue;

            //find DoctorHolidays instance associated with doctor dup.getDoctor()
            DoctorHolidays dh = findDhByDoctor(dup.getDoctor());

            ContextConstraint context = new ContextConstraint(dup, concreteShift, dh, holidays);
            if(verifyAllConstraints(context, false)){
                doctorList.add(dup.getDoctor());
                dup.addConcreteShift(context.getConcreteShift());
                //Creo il Doctor Assignement
                int indexTask=selectedTask %(concreteShift.getShift().getMedicalService().getTasks().size());
                DoctorAssignment da = new DoctorAssignment(dup.getDoctor(),
                                                             status,
                                                             concreteShift,
                                                         concreteShift.getShift().getMedicalService().getTasks().get(indexTask));
                //lo inserisco nei concrateShift
                concreteShift.getDoctorAssignmentList().add(da);
                selectedTask++;
            }
            List<Doctor> contextDoctorsOnDuty = DoctorAssignmentUtil.getDoctorsInConcreteShift(context.getConcreteShift(), Collections.singletonList(status));

            //here we need to modify only the appropriate queues
            if(contextDoctorsOnDuty.size() < qss.getValue()){
                dup.updatePriority(PriorityQueueEnum.GENERAL);
                if(prevConcreteShiftDup != null && prevConcreteShiftDup.contains(dup))
                    dup.updatePriority(PriorityQueueEnum.LONG_SHIFT);
                else if(concreteShift.getShift().getTimeSlot() == TimeSlot.NIGHT)
                    dup.updatePriority(PriorityQueueEnum.NIGHT);

            }
            selectedUsers++;

        }
        // Case in which the algorithm ends without having found enough doctors to place into the concrete shift
        if (selectedUsers != qss.getValue()){
            throw new NotEnoughFeasibleUsersException(qss.getValue(), selectedUsers);
        }

    }


    /**
     * This private method retrieves the eventual concrete shift associated to a specific date and a specific timeSlot.
     * It is useful to check if there is the possibility for a doctor to be assigned to a concrete shift in the morning
     * and to a concrete shift in the afternoon in the same day (case of long shift).
     * @param epochDate Date of the concrete shift
     * @param timeSlot TimeSlot of the concrete shift (morning, afternoon or night)
     * @return The concrete shift associated to epochDate and timeSlot, if it exists. If it does not exist, a null value will be returned.
     */
    private ConcreteShift getConcreteShift(long epochDate, TimeSlot timeSlot) {

        for(ConcreteShift concreteShift : this.schedule.getConcreteShifts()) {
            if(concreteShift.getDate() == epochDate && concreteShift.getShift().getTimeSlot() == timeSlot)
                return concreteShift;

        }

        return null;

    }


    /**
     * This private method retrieves a list of DoctorUffaPriority instances associated the doctors present in the concrete shift
     * specified as parameter.
     * @param concreteShift The concrete shift from which the (not removed) doctors have to be extracted
     * @param allDoctorUffaPriority List of DoctorUffaPriority instances associated to all doctors
     * @return A list of DoctorUffaPriority instances
     */
    private List<DoctorUffaPriority> getDupFromConcreteShift(ConcreteShift concreteShift, List<DoctorUffaPriority> allDoctorUffaPriority) {

        List<DoctorUffaPriority> concreteShiftDup = new ArrayList<>();
        for(DoctorAssignment doctorAssignment : concreteShift.getDoctorAssignmentList()) {
            for(DoctorUffaPriority dup : allDoctorUffaPriority) {
                if (doctorAssignment.getDoctor().equals(dup.getDoctor()))
                    concreteShiftDup.add(dup);

            }

        }
        return concreteShiftDup;

    }


    /**
     * This private method retrieves the DoctorHolidays instance associated to a specific doctor.
     * @param doctor Doctor related to the DoctorHolidays instance we want to extract
     * @return DoctorHolidays instance
     */
    private DoctorHolidays findDhByDoctor(Doctor doctor) {

        for(DoctorHolidays dh : doctorHolidaysList) {
            if (dh.getDoctor().equals(doctor))
                return dh;

        }
        return null;

    }



    /**
     * This method applies all the constraints to the specified context. If a constraint is violated, then it is added
     * to the log. Moreover, if the violated constraint is hard, then the shift schedule is marked as illegal and the
     * cause of the violation is initialized.
     * @param context Context in which all the constraints are applied and verified
     * @param isForced Boolean that represents if it is possible to violate the soft constraints
     * @return True if there are no violations or the only verified violations are soft with isForced==true; false otherwise
     */
    private boolean verifyAllConstraints(ContextConstraint context, boolean isForced){

        //This flag indicates if there has been a violation in the constraints.
        boolean isOk = true;

        for(Constraint constraint : this.allConstraints){
            try {
                constraint.verifyConstraint(context);
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
            //find DoctorHolidays instance associated with current doctor
            DoctorHolidays dh = findDhByDoctor(doctor);
            if (!verifyAllConstraints(new ContextConstraint(this.allDoctorUffaPriorities.get(doctor.getId()), concreteShift, dh, holidays), isForced)){
                schedule.setCauseIllegal(new IllegalAssegnazioneTurnoException("Un vincolo stringente è stato violato, oppure un vincolo non stringente è stato violato e non è stato richiesto di forzare l'assegnazione. Consultare il log delle violazioni della pianificazione può aiutare a investigare la causa."));
            }
        }
        if(schedule.getCauseIllegal() == null){

            for (DoctorAssignment da : concreteShift.getDoctorAssignmentList()){
                Doctor doctor = da.getDoctor();
                this.allDoctorUffaPriorities.get(doctor.getId()).addConcreteShift(concreteShift);
                List<Doctor> doctorsOnDuty = DoctorAssignmentUtil.getDoctorsInConcreteShift(concreteShift, Collections.singletonList(ConcreteShiftDoctorStatus.ON_DUTY));

                /* If the concrete shift we are populating is in the afternoon, we get the eventual concrete shift allocated in the morning of the same day.
                 * It is important to check if there is someone who can be allocated in a long shift (shift in the morning + shift in the afternoon of the same day).
                 */
                ConcreteShift prevConcreteShift = null;
                List<DoctorUffaPriority> prevConcreteShiftDup = null;
                if(concreteShift.getShift().getTimeSlot() == TimeSlot.AFTERNOON) {
                    prevConcreteShift = this.getConcreteShift(concreteShift.getDate(), TimeSlot.MORNING);
                    if(prevConcreteShift != null)   //case in which there exists a concrete shift the same day in the morning
                        prevConcreteShiftDup = this.getDupFromConcreteShift(prevConcreteShift, new ArrayList<>(this.allDoctorUffaPriorities.values()));

                }


                if(doctorsOnDuty.contains(doctor)) {
                    //here we need to modify only the appropriate queues
                    this.allDoctorUffaPriorities.get(doctor.getId()).updatePriority(PriorityQueueEnum.GENERAL);
                    if(prevConcreteShiftDup != null && prevConcreteShiftDup.contains(this.allDoctorUffaPriorities.get(doctor.getId())))
                        this.allDoctorUffaPriorities.get(doctor.getId()).updatePriority(PriorityQueueEnum.LONG_SHIFT);
                    else if(concreteShift.getShift().getTimeSlot() == TimeSlot.NIGHT)
                        this.allDoctorUffaPriorities.get(doctor.getId()).updatePriority(PriorityQueueEnum.NIGHT);

                }

            }
            this.schedule.getConcreteShifts().add(concreteShift);
        }

        return this.schedule;

    }
}
