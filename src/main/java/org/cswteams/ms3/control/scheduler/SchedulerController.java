package org.cswteams.ms3.control.scheduler;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;

import org.cswteams.ms3.control.scocciatura.ControllerScocciatura;
import org.cswteams.ms3.control.utils.DoctorAssignmentUtil;
import org.cswteams.ms3.dao.*;
import org.cswteams.ms3.dto.ModifyConcreteShiftDTO;
import org.cswteams.ms3.dto.RegisterConcreteShiftDTO;
import org.cswteams.ms3.dto.ScheduleDTO;
import org.cswteams.ms3.dto.showscheduletoplanner.ShowScheduleToPlannerDTO;
import org.cswteams.ms3.dto.user.UserCreationDTO;
import org.cswteams.ms3.entity.*;
import org.cswteams.ms3.entity.constraint.Constraint;
import org.cswteams.ms3.entity.scocciature.Scocciatura;
import org.cswteams.ms3.enums.ConcreteShiftDoctorStatus;
import org.cswteams.ms3.exception.ConcreteShiftException;
import org.cswteams.ms3.exception.IllegalScheduleException;
import org.cswteams.ms3.utils.DateConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

// TODO: Generate concrete shift controller from this class
/**
 * {@code SchedulerController} è un {@code @Service} e rappresenta l'orchestratore principale della pipeline di generazione e gestione degli schedule.
 * È responsabile di:
 *  - Raccogliere dati dai vari DAO (Data Access Object)</li>
 *  - Costruire il contesto necessario per la schedulazione</li>
 *  - Invocare il motore {@link ScheduleBuilder} per la creazione dello schedule</li>
 *  - Persistere i risultati delle operazioni</li>
 * Agisce come ponte tra il layer REST ({@link ScheduleRestEndpoint}) e il motore di scheduling vero e proprio ({@link ScheduleBuilder}).
 *
 * Per una descrizione dettagliata del flusso di schedulazione e dei componenti coinvolti, si vedano:
 * @see docs/scheduling_flow/README.md
 * @see docs/AI_powered_rescheduling/sprint_4/story_1.md#microtask-11--backend
 * @see docs/AI_powered_rescheduling/sprint_4/story_1.md#_13-implementazione-schedulercontroller
 * @see docs/AI_powered_rescheduling/sprint_4/story_1.md#_12-application-orchestrator-ischedulercontroller
 * @see docs/AI_powered_rescheduling/sprint_4/story_1.md#microtask-12--vincoli-e-pipeline-priorità-baseline
 */

@Service
public class SchedulerController implements ISchedulerController {
    private static final Logger logger = LoggerFactory.getLogger(SchedulerController.class);

    @Autowired
    private DoctorDAO doctorDAO;

    @Autowired
    private ShiftDAO shiftDAO;

    @Autowired
    private DoctorAssignmentDAO doctorAssignmentDAO;

    @Autowired
    private ScheduleDAO scheduleDAO;

    @Autowired
    private ConstraintDAO constraintDAO;

    @Autowired
    private ConcreteShiftDAO concreteShiftDAO;

    @Autowired
    private ScocciaturaDAO scocciaturaDAO;

    @Autowired
    private HolidayDAO holidayDAO;

    @Autowired
    private DoctorHolidaysDAO doctorHolidaysDAO;

    @Autowired
    private DoctorUffaPriorityDAO doctorUffaPriorityDAO;

    @Autowired
    private DoctorUffaPrioritySnapshotDAO doctorUffaPrioritySnapshotDAO;

    @Autowired
    private TaskDAO taskDAO;

    private ScheduleBuilder scheduleBuilder;

    @Override
    public Set<ShowScheduleToPlannerDTO> getAllSchedulesWithDates(){
        Set<ShowScheduleToPlannerDTO> showScheduleToPlannerDTOSet = new HashSet<>();
        List<Schedule> allGeneratedSchedules = scheduleDAO.findAll();
        for(Schedule singleSchedule : allGeneratedSchedules){
            ShowScheduleToPlannerDTO showScheduleToPlannerDTO = new ShowScheduleToPlannerDTO(
                    singleSchedule.getId(),
                    DateConverter.convertEpochToDateString(singleSchedule.getStartDate()),
                    DateConverter.convertEpochToDateString(singleSchedule.getEndDate()),
                    !singleSchedule.getViolatedConstraints().isEmpty()
            );
            showScheduleToPlannerDTOSet.add(showScheduleToPlannerDTO);
        }

        return showScheduleToPlannerDTOSet;

    }


    /**
     * Implementazione della versione "proxy" del metodo {@code createSchedule(start, end)}.
     * Questo metodo carica le priorità correnti e gli snapshot delle priorità dei medici
     * e poi invoca la variante completa {@link #createSchedule(LocalDate, LocalDate, List, List)}
     * per eseguire la pipeline di generazione dello schedule.
     *
     * @param startDate Data di inizio dello schedule.
     * @param endDate Data di fine dello schedule.
     * @return Lo schedule generato o {@code null} se la generazione non è possibile.
     * @see docs/AI_powered_rescheduling/sprint_4/story_1.md#_32-createschedulestart-end-proxy
     */
    @Override
    @Transactional
    public Schedule createSchedule(LocalDate startDate, LocalDate endDate) {
        List<DoctorUffaPriority> doctorUffaPriorityList = doctorUffaPriorityDAO.findAll();
        List<DoctorUffaPrioritySnapshot> doctorUffaPrioritySnapshot = doctorUffaPrioritySnapshotDAO.findAll();
        return createScheduleInternal(startDate, endDate, doctorUffaPriorityList, doctorUffaPrioritySnapshot, true);
    }

    @Override
    @Transactional(Transactional.TxType.SUPPORTS)
    public Schedule createScheduleTransient(LocalDate startDate, LocalDate endDate) {
        List<DoctorUffaPriority> doctorUffaPriorityList = cloneDoctorUffaPriorities(doctorUffaPriorityDAO.findAll());
        List<DoctorUffaPrioritySnapshot> doctorUffaPrioritySnapshot =
                cloneDoctorUffaPrioritySnapshots(doctorUffaPrioritySnapshotDAO.findAll());
        return createScheduleInternal(startDate, endDate, doctorUffaPriorityList, doctorUffaPrioritySnapshot, false);
    }



    /**
     * Implementazione completa della pipeline di generazione di uno schedule di turni.
     * Questo metodo è il cuore dell'orchestratore {@code SchedulerController} e coordina
     * l'intero processo di creazione dello schedule, dalla raccolta dati alla persistenza.
     *
     * @param startDate Data di inizio dello schedule.
     * @param endDate Data di fine dello schedule.
     * @param doctorUffaPriorityList Lista delle priorità UFFA attuali dei medici.
     * @param snapshot Snapshot delle priorità UFFA per scopi di rigenerazione.
     * @return Lo schedule generato, o {@code null} se la generazione fallisce o non è consentita.
     * @throws IllegalScheduleException se lo schedule generato non rispetta i vincoli.
     * @see docs/AI_powered_rescheduling/sprint_4/story_1.md#_33-createschedulestart-end-priorities-snapshot-full-pipeline
     * @see docs/AI_powered_rescheduling/sprint_4/story_1.md#microtask-12--vincoli-e-pipeline-priorità-baseline
     * @see docs/AI_powered_rescheduling/sprint_4/story_1.md#_2-data-sources-toccate-dao-durante-la-generazione
     */
    @Override
    @Transactional
    public Schedule createSchedule(LocalDate startDate, LocalDate endDate, List<DoctorUffaPriority> doctorUffaPriorityList, List<DoctorUffaPrioritySnapshot> snapshot)  {
        return createScheduleInternal(startDate, endDate, doctorUffaPriorityList, snapshot, true);
    }

    private Schedule createScheduleInternal(LocalDate startDate,
                                            LocalDate endDate,
                                            List<DoctorUffaPriority> doctorUffaPriorityList,
                                            List<DoctorUffaPrioritySnapshot> snapshot,
                                            boolean persist) {
        String mode = resolvePlanMode();
        long flowStart = System.currentTimeMillis();

        // Input gate for first-time planning: the initial baseline schedule cannot start in the past.
        boolean hasExistingSchedules = !scheduleDAO.findAll().isEmpty();
        if (!hasExistingSchedules && startDate.isBefore(LocalDate.now())) {
            logEvent(eventName(mode, "start_rejected"), mode, Map.of(
                    "durationMs", System.currentTimeMillis() - flowStart,
                    "errorType", "PAST_INITIAL_SCHEDULE"
            ));
            return null; // non consentire schedulazioni iniziali nel passato
        }

        // Check if there already exists a shift schedule for the dates we want to plan.
        if(alreadyExistsAnotherSchedule(startDate,endDate)) {
            logEvent(eventName(mode, "start_rejected"), mode, Map.of(
                    "durationMs", System.currentTimeMillis() - flowStart,
                    "errorType", "DUPLICATE_RANGE"
            ));
            return null;
        }

        // Orchestration step 1 (input expansion): transform [startDate, endDate] into dated concrete shifts.
        // currentDay = date used to iterate on the dates interval (start date -> end date)
        LocalDate currentDay = startDate;
        List<ConcreteShift> allConcreteShifts = new ArrayList<>();
        long dataLoadStart = System.currentTimeMillis();
        List<Shift> shifts = shiftDAO.findAll();
        long concreteShiftBuildStart = System.currentTimeMillis();

        //Creation of the concrete shifts by associating a date foreach shift
        //Iteration on all the dates of the interval. Foreach date, we iterate on all the shifts.
        while(!currentDay.isAfter(endDate)){
            for(Shift shift : shifts){

                //We can assign this shift to a certain date only if the corresponding day of week is admissible for the shift.
                if (shift.getDaysOfWeek().contains(currentDay.getDayOfWeek())){
                    allConcreteShifts.add(new ConcreteShift(currentDay.toEpochDay(), shift));
                }

            }
            //We move on the next day.
            currentDay = currentDay.plusDays(1);
        }
        logEvent(eventName(mode, "concrete_shifts_built"), mode, Map.of(
                "durationMs", System.currentTimeMillis() - concreteShiftBuildStart,
                "shiftsCount", shifts.size(),
                "concreteShiftsCount", allConcreteShifts.size()
        ));

        List<Constraint> constraints = constraintDAO.findAll();
        List<Doctor> doctors = doctorDAO.findAll();
        List<Holiday> holidays = holidayDAO.findAll();
        List<DoctorHolidays> doctorHolidays = doctorHolidaysDAO.findAll();
        List<Scocciatura> scocciaturaList = scocciaturaDAO.findAll();
        long dataLoadDuration = System.currentTimeMillis() - dataLoadStart;
        logEvent(eventName(mode, "data_loaded"), mode, Map.of(
                "durationMs", dataLoadDuration,
                "shiftsCount", shifts.size(),
                "concreteShiftsCount", allConcreteShifts.size(),
                "constraintsCount", constraints.size(),
                "doctorsCount", doctors.size(),
                "holidaysCount", holidays.size(),
                "doctorHolidaysCount", doctorHolidays.size(),
                "prioritiesCount", doctorUffaPriorityList.size(),
                "snapshotCount", snapshot.size(),
                "scocciaturaCount", scocciaturaList.size()
        ));

        //Creation of a schedule builder foreach new shift schedule
        try {
            long builderInitStart = System.currentTimeMillis();
            this.scheduleBuilder = new ScheduleBuilder(
                startDate,                      //Start date of the shift schedule
                endDate,                        //End date of the shift schedule
                constraints,                    //All the constraints to respect when a doctor is assigned to a concrete shift
                allConcreteShifts,              //Concrete shifts (without doctors)
                doctors,                        //All the possible doctors who can be assigned to the concrete shifts
                holidays,                       //All the holidays saved in the db
                doctorHolidays,                 //All the associations between doctors and holidays
                doctorUffaPriorityList,         //All the information about priority levels on all the queues of the doctors
                snapshot                        //Snapshot to update to save actual priorities
                );
            logEvent(eventName(mode, "builder_initialized"), mode, Map.of(
                    "durationMs", System.currentTimeMillis() - builderInitStart,
                    "constraintsCount", constraints.size(),
                    "doctorsCount", doctors.size(),
                    "concreteShiftsCount", allConcreteShifts.size()
            ));

            long prioritiesStart = System.currentTimeMillis();
            ControllerScocciatura controllerScocciatura = new ControllerScocciatura(scocciaturaList);
            //We set the controller that manages doctors priorities.
            this.scheduleBuilder.setControllerScocciatura(controllerScocciatura);
            logEvent(eventName(mode, "constraints_priorities_ready"), mode, Map.of(
                    "durationMs", System.currentTimeMillis() - prioritiesStart,
                    "constraintsCount", constraints.size(),
                    "scocciaturaCount", scocciaturaList.size(),
                    "prioritiesCount", doctorUffaPriorityList.size()
            ));

            long buildStart = System.currentTimeMillis();
            // [BASELINE-FLOW] AI HOOK:
            // Questo è il punto di biforcazione.
            // Attualmente si chiama il builder deterministico.
            // Qui in futuro: if (mode == AI) callAiAgent() else scheduleBuilder.build().
            Schedule schedule = this.scheduleBuilder.build();
            logEvent(eventName(mode, "schedule_built"), mode, Map.of(
                    "durationMs", System.currentTimeMillis() - buildStart,
                    "concreteShiftsCount", schedule.getConcreteShifts().size(),
                    "violatedConstraintsCount", schedule.getViolatedConstraints().size()
            ));

            if (persist) {
                long scheduleSaveStart = System.currentTimeMillis();
                scheduleDAO.save(schedule);
                logEvent(eventName(mode, "schedule_saved"), mode, Map.of(
                        "durationMs", System.currentTimeMillis() - scheduleSaveStart,
                        "planId", schedule.getId()
                ));
                long prioritiesSaveStart = System.currentTimeMillis();
                for(DoctorUffaPriority dup: schedule.getDoctorUffaPriorityList()) {
                    dup.setSchedule(schedule);
                    doctorUffaPriorityDAO.save(dup);
                }
                logEvent(eventName(mode, "priorities_saved"), mode, Map.of(
                        "durationMs", System.currentTimeMillis() - prioritiesSaveStart,
                        "planId", schedule.getId(),
                        "savedPrioritiesCount", schedule.getDoctorUffaPriorityList().size()
                ));
                logEvent(eventName(mode, "persisted"), mode, Map.of(
                        "durationMs", System.currentTimeMillis() - scheduleSaveStart,
                        "planId", schedule.getId(),
                        "savedPrioritiesCount", schedule.getDoctorUffaPriorityList().size()
                ));
            }

            return schedule;

        } catch (IllegalScheduleException e) {
            logEvent(eventName(mode, "failed"), mode, Map.of(
                    "durationMs", System.currentTimeMillis() - flowStart,
                    "errorType", e.getClass().getSimpleName(),
                    "errorCode", "ILLEGAL_SCHEDULE"
            ));
            return null;
        }

    }

    @Override
    @Transactional
    public Schedule persistSchedule(Schedule schedule) {
        if (schedule == null) {
            return null;
        }
        scheduleDAO.save(schedule);
        List<DoctorUffaPriority> priorities = schedule.getDoctorUffaPriorityList();
        if (priorities != null) {
            for (DoctorUffaPriority dup : priorities) {
                dup.setSchedule(schedule);
                doctorUffaPriorityDAO.save(dup);
            }
        }
        return schedule;
    }

    /**
     * Esegue la rigenerazione di uno schedule esistente.
     * Questa operazione carica lo schedule specificato, ripristina le priorità dei medici
     * allo stato dello snapshot, elimina lo schedule precedente e ne genera uno nuovo
     * con lo stesso intervallo di date.
     *
     * @param id L'ID dello schedule da rigenerare.
     * @return {@code true} se la rigenerazione ha avuto successo, {@code false} altrimenti.
     * @see docs/AI_powered_rescheduling/sprint_4/story_1.md#_42-recreatescheduleid
     */
    @Override
    public boolean recreateSchedule(long id) {
        String mode = resolvePlanMode();
        long flowStart = System.currentTimeMillis();
        Optional<Schedule> optionalSchedule = scheduleDAO.findById(id);

        if(optionalSchedule.isEmpty()) {
            logEvent(eventName(mode, "data_loaded"), mode, Map.of(
                    "durationMs", System.currentTimeMillis() - flowStart,
                    "planId", id,
                    "phase", "schedule_lookup",
                    "result", "not_found"
            ));
            return false;
        }

        Schedule schedule = optionalSchedule.get();
        LocalDate startDate = LocalDate.ofEpochDay(schedule.getStartDate());
        LocalDate endDate = LocalDate.ofEpochDay(schedule.getEndDate());

        List<DoctorUffaPrioritySnapshot> doctorUffaPrioritySnapshot = doctorUffaPrioritySnapshotDAO.findAll();
        List<DoctorUffaPriority> doctorUffaPriorityList = doctorUffaPriorityDAO.findAll();
        logEvent(eventName(mode, "data_loaded"), mode, Map.of(
                "durationMs", System.currentTimeMillis() - flowStart,
                "planId", id,
                "phase", "priorities_loaded",
                "prioritiesCount", doctorUffaPriorityList.size(),
                "snapshotCount", doctorUffaPrioritySnapshot.size()
        ));

        /* Restore priorities to snapshot */
        long prioritiesRestoreStart = System.currentTimeMillis();
        for (DoctorUffaPrioritySnapshot dupSnapshot : doctorUffaPrioritySnapshot) {
            for (DoctorUffaPriority dup : doctorUffaPriorityList) {
                if (dupSnapshot.getDoctor().equals(dup.getDoctor())) {
                    int generalPriority = dupSnapshot.getGeneralPriority();
                    int longShiftPriority = dupSnapshot.getLongShiftPriority();;
                    int nightPriority = dupSnapshot.getNightPriority();

                    dup.setGeneralPriority(generalPriority);
                    dup.setNightPriority(nightPriority);
                    dup.setLongShiftPriority(longShiftPriority);
                }
            }
        }
        logEvent(eventName(mode, "priorities_restored"), mode, Map.of(
                "durationMs", System.currentTimeMillis() - prioritiesRestoreStart,
                "planId", id,
                "prioritiesCount", doctorUffaPriorityList.size()
        ));

        //It is not allowed to remove a shift schedule in the past.
        if(!removeSchedule(id)) {
            logEvent(eventName(mode, "remove_failed"), mode, Map.of(
                    "durationMs", System.currentTimeMillis() - flowStart,
                    "planId", id,
                    "errorType", "REMOVE_SCHEDULE_FAILED"
            ));
            return false;
        }
        logEvent(eventName(mode, "removed"), mode, Map.of(
                "durationMs", System.currentTimeMillis() - flowStart,
                "planId", id
        ));

        Schedule newSchedule = createSchedule(startDate,endDate, doctorUffaPriorityList, doctorUffaPrioritySnapshot);
        if (newSchedule != null && newSchedule.getId() != null) {
            MDC.put("newPlanId", String.valueOf(newSchedule.getId()));
        }
        return true;
    }

    private String resolvePlanMode() {
        String mode = MDC.get("planMode");
        return mode != null ? mode : "generate";
    }

    private String eventName(String mode, String baseEvent) {
        return "plan_" + mode + "_" + baseEvent;
    }

    private void logEvent(String event, String mode, Map<String, Object> fields) {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("event", event);
        data.put("requestId", getRequestId());
        data.put("mode", mode);
        data.putAll(fields);
        logger.info(formatLogMessage(data));
    }

    private String getRequestId() {
        String requestId = MDC.get("requestId");
        return requestId != null ? requestId : "unknown";
    }

    private String formatLogMessage(Map<String, Object> fields) {
        StringBuilder builder = new StringBuilder();
        for (Map.Entry<String, Object> entry : fields.entrySet()) {
            if (builder.length() > 0) {
                builder.append(' ');
            }
            builder.append(entry.getKey()).append('=').append(formatValue(entry.getValue()));
        }
        return builder.toString();
    }

    private String formatValue(Object value) {
        if (value == null) {
            return "null";
        }
        if (value instanceof Number || value instanceof Boolean) {
            return value.toString();
        }
        String text = value.toString();
        if (text.contains(" ") || text.contains("=")) {
            return '"' + text.replace("\"", "\\\"") + '"';
        }
        return text;
    }

    @Override
    public Schedule addConcreteShift(ConcreteShift concreteShift, boolean forced) throws IllegalScheduleException {

        Schedule schedule;

        //We create a new builder passing him as parameter an existing shift schedule.
        this.scheduleBuilder = new ScheduleBuilder(
                constraintDAO.findAll(),            //All the constraints that shall be respected when a doctor is assigned to a concrete shift
                doctorUffaPriorityDAO.findAll(),    //All the possible doctors that can be assigned to the concrete shifts
                scheduleDAO.findByDateBetween(concreteShift.getDate()),  //Existing shift schedule
                doctorHolidaysDAO.findAll(),
                holidayDAO.findAll()
        );

        schedule = this.scheduleBuilder.addConcreteShift(concreteShift,forced);

        //We commit changes to schedule only if they do not taint it
        if (schedule.getCauseIllegal() == null){
            scheduleDAO.flush();
        }
        return schedule;

    }

    @Override
    public void removeConcreteShiftFromSchedule(ConcreteShift concreteShiftOld) {
        Schedule schedule = scheduleDAO.findByDateBetween(concreteShiftOld.getDate());
        schedule.getConcreteShifts().remove(concreteShiftOld);
        scheduleDAO.flush();
    }

    @Override
    public boolean removeConcreteShift(Long concreteShiftId) {
       Optional<ConcreteShift> concreteShift = concreteShiftDAO.findById(concreteShiftId);
        if(concreteShift.isEmpty())
            return false;

        this.removeConcreteShiftFromSchedule(concreteShift.get());
        concreteShiftDAO.delete(concreteShift.get());
        return true;
    }

    @Override
    public Schedule addConcreteShift(RegisterConcreteShiftDTO registerConcreteShiftDTO, boolean forced) throws ConcreteShiftException, IllegalScheduleException {

        //We need a shift which is present in the database in order to convert the DTO into an entity.
        List<Shift> shiftsList = shiftDAO.findAllByMedicalServiceLabelAndTimeSlot(registerConcreteShiftDTO.getServizio().getName(), registerConcreteShiftDTO.getTimeSlot());
        if(shiftsList.isEmpty())
            throw new ConcreteShiftException("A shift with the specified services does not exist.");
        Shift shift = null;
        for(Shift shiftDB: shiftsList){
            //if(shiftDB.getMansione().equals(registerConcreteShiftDTO.getMansione())){
            if(shiftDB.getMedicalService().getLabel().equals(registerConcreteShiftDTO.getServizio().getName())) {
                shift = shiftDB;
                break;
            }
        }
        if(shift == null){
            throw new ConcreteShiftException("A shift with the specified services does not exist.");
        }

        ConcreteShift concreteShift = new ConcreteShift(
                ChronoUnit.DAYS.between(LocalDate.of(1970, 1, 1), LocalDate.of(registerConcreteShiftDTO.getYear(), registerConcreteShiftDTO.getMonth(), registerConcreteShiftDTO.getDay())),
                shift
        );
        //definition of doctorAssignmentList to set into concreteShift

        for (Task task : shift.getMedicalService().getTasks()) {
            for (Doctor onCallDoctor : usersDTOtoEntity(registerConcreteShiftDTO.getOnCallDoctors())) {
                //check if onCallDoctor is already in doctorAssignmentList
                boolean isAssigned = false;
                for(DoctorAssignment da : concreteShift.getDoctorAssignmentList()) {
                    if (onCallDoctor == da.getDoctor()) {
                        isAssigned = true;
                        break;
                    }
                }
                if(!isAssigned)
                    concreteShift.getDoctorAssignmentList().add(new DoctorAssignment(onCallDoctor, ConcreteShiftDoctorStatus.ON_CALL, concreteShift, task));
            }

            for (Doctor onDutyDoctor : usersDTOtoEntity(registerConcreteShiftDTO.getOnDutyDoctors())) {
                //check if onCallDoctor is already in doctorAssignmentList
                boolean isAssigned = false;
                for(DoctorAssignment da : concreteShift.getDoctorAssignmentList()) {
                    if (onDutyDoctor == da.getDoctor()) {
                        isAssigned = true;
                        break;
                    }
                }
                if(!isAssigned)
                    concreteShift.getDoctorAssignmentList().add(new DoctorAssignment(onDutyDoctor, ConcreteShiftDoctorStatus.ON_DUTY, concreteShift, task));
            }
        }

        if(!checkDoctorsOnConcreteShift(concreteShift)){
            throw new ConcreteShiftException("Collision between on-call and on-duty doctors.");
        }

        return this.addConcreteShift(concreteShift,forced);

    }

    /**
     * This private method checks if there is the same doctor on duty and on call for the same concreteShift.
     * @param concreteShift The concrete shift on which we want to do the check
     * @return Boolean that represents if the check was successful
     */
    private boolean checkDoctorsOnConcreteShift(ConcreteShift concreteShift) {

        List<Doctor> doctorsOnDuty = DoctorAssignmentUtil.getDoctorsInConcreteShift(concreteShift, Collections.singletonList(ConcreteShiftDoctorStatus.ON_DUTY));
        List<Doctor> doctorsOnCall = DoctorAssignmentUtil.getDoctorsInConcreteShift(concreteShift, Collections.singletonList(ConcreteShiftDoctorStatus.ON_CALL));

        for(Doctor doctor1 : doctorsOnDuty){
            for(Doctor doctor2 : doctorsOnCall){
                if (doctor1.getId().longValue() == doctor2.getId().longValue()){
                    // Legality check: the same doctor cannot be simultaneously ON_DUTY and ON_CALL in one shift.
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    @Transactional
    public Schedule modifyConcreteShift(ModifyConcreteShiftDTO modifyConcreteShiftDTO) throws IllegalScheduleException {

        if(concreteShiftDAO.findById(modifyConcreteShiftDTO.getConcreteShiftId()).isPresent()) {
            ConcreteShift concreteShiftOld = concreteShiftDAO.findById(modifyConcreteShiftDTO.getConcreteShiftId()).get();
            ConcreteShift concreteShiftNew = concreteShiftOld.clone();

            //Retrieve of all the doctors that were originally assigned to the concrete shift
            List<ConcreteShiftDoctorStatus> status = new ArrayList<>();
            status.add(ConcreteShiftDoctorStatus.ON_DUTY);
            status.add(ConcreteShiftDoctorStatus.ON_CALL);

            List<Doctor> allDoctorsOld = DoctorAssignmentUtil.getDoctorsInConcreteShift(concreteShiftOld, status);

            //If necessary, we modify doctors on duty list. This requires an initial squash of this list.
            if (modifyConcreteShiftDTO.getOnDutyDoctors() != null) {
                //The squash
                concreteShiftNew.getDoctorAssignmentList().removeIf(da -> da.getConcreteShiftDoctorStatus() == ConcreteShiftDoctorStatus.ON_DUTY);
                //The update
                for (long onDutyId : modifyConcreteShiftDTO.getOnDutyDoctors()) {
                    Task involvedTask = doctorAssignmentDAO.findByDoctorAndConcreteShift(doctorDAO.findById(onDutyId), concreteShiftNew).getTask();
                    DoctorAssignment newDoctorAssignment = new DoctorAssignment(doctorDAO.findById(onDutyId), ConcreteShiftDoctorStatus.ON_DUTY, concreteShiftNew, involvedTask);
                    concreteShiftNew.getDoctorAssignmentList().add(newDoctorAssignment);

                }
            }

            //If necessary, we modify doctors on call list. This requires an initial squash of this list.
            if (modifyConcreteShiftDTO.getOnCallDoctors() != null) {
                //The squash
                concreteShiftNew.getDoctorAssignmentList().removeIf(da -> da.getConcreteShiftDoctorStatus() == ConcreteShiftDoctorStatus.ON_CALL);
                //The update
                for (long onCallId : modifyConcreteShiftDTO.getOnCallDoctors()) {
                    Task involvedTask = doctorAssignmentDAO.findByDoctorAndConcreteShift(doctorDAO.findById(onCallId), concreteShiftNew).getTask();
                    DoctorAssignment newDoctorAssignment = new DoctorAssignment(doctorDAO.findById(onCallId), ConcreteShiftDoctorStatus.ON_CALL, concreteShiftNew, involvedTask);
                    concreteShiftNew.getDoctorAssignmentList().add(newDoctorAssignment);

                }
            }

            //Retrieve of all the doctors that are now assigned to the concrete shift
            List<Doctor> allDoctorsNew = DoctorAssignmentUtil.getDoctorsInConcreteShift(concreteShiftNew, status);

            /*
            We register the doctors that were allocated in the old concrete shift and are not present in the new concrete
            shift as removed doctors.
            */
            for (Doctor doctor : allDoctorsOld) {
                if (!allDoctorsNew.contains(doctor)) {
                    Task involvedTask = doctorAssignmentDAO.findByDoctorAndConcreteShift(doctor, concreteShiftOld).getTask();
                    DoctorAssignment newDoctorAssignment = new DoctorAssignment(doctor, ConcreteShiftDoctorStatus.REMOVED, concreteShiftNew, involvedTask);
                    concreteShiftNew.getDoctorAssignmentList().add(newDoctorAssignment);

                }
            }

            //We remove the old concrete shift and replace it with the new one.
            this.removeConcreteShiftFromSchedule(concreteShiftOld);
            Schedule schedule = this.addConcreteShift(concreteShiftNew, true);

            //If some constraints are violated, we restore the old concrete shift.
            if (schedule.getCauseIllegal() != null) {
                schedule.getConcreteShifts().add(concreteShiftOld);
                scheduleDAO.flush();
            } else {
                //In the other case, we delete the old concrete shift from the database too.
                this.removeConcreteShift(concreteShiftOld.getId());
            }

            return schedule;

        }

        return null;    //TODO: establish if it is necessary to adjust this null value.

    }

    @Override
    public List<ScheduleDTO> readSchedules(){
        return scheduleEntitytoDTO(scheduleDAO.findAll());
    }

    @Override
    public List<ScheduleDTO> readIllegalSchedules() {
        return scheduleEntitytoDTO(scheduleDAO.leggiSchedulazioniIllegali());
    }


    @Override
    public boolean removeSchedule(long id){

        Optional<Schedule> scheduleOptional = scheduleDAO.findById(id);

        if(scheduleOptional.isEmpty())
            return false;

        //Verifico se lo schedulo che voglio eliminare è uno schedulo futuro e non passato
        if(LocalDate.ofEpochDay(scheduleOptional.get().getEndDate()).isBefore(LocalDate.now()))
            return false;

        //Deletion of Schedule into DoctorUffaPriority instances in order to not violate foreign key constraints in the db
        for(DoctorUffaPriority dup : doctorUffaPriorityDAO.findAll()) {
            dup.setSchedule(null);
            doctorUffaPriorityDAO.save(dup);
        }
        scheduleDAO.deleteById(id);

        return true;

    }


    /**
     * This method verifies the existence of a shift schedule for the dates we want to plan.
     * @param startNewSchedule First date to be planned for a shift schedule
     * @param endNewSchedule Last date to be planned for a shift schedule
     * @return False if there already exists a shift schedule for the dates we want to plan, true otherwise
     */
    public boolean alreadyExistsAnotherSchedule(LocalDate startNewSchedule, LocalDate endNewSchedule){
        List<Schedule> allSchedule = scheduleDAO.findAll();

        for (Schedule schedule : allSchedule) {
            LocalDate existingStart = LocalDate.ofEpochDay(schedule.getStartDate());
            LocalDate existingEnd = LocalDate.ofEpochDay(schedule.getEndDate());

            // Behavior intentionally retained after revert cycles:
            // we currently block only exact duplicate intervals and allow overlaps/adjacent ranges.
            // This matches the effective business rule used in the current release branch.
            if (existingStart.equals(startNewSchedule) && existingEnd.equals(endNewSchedule)) {
                return true;
            }
        }
        return false;

    }
    /**
     * This private method converts an instance of Schedule into a DTO. It supports the work of scheduleEntitytoDTO() method.
     * @param schedule Schedule instance to be converted into DTO
     * @return ScheduleDTO instance
     */
    private static ScheduleDTO scheduleToDTO(Schedule schedule) {
        boolean isIllegal = schedule.getCauseIllegal()!=null;
        return new ScheduleDTO(schedule.getStartDate(), schedule.getEndDate(), isIllegal, schedule.getId());
    }

    /**
     * This private method converts a list of Schedule instances into a list of DTOs.
     * @param scheduleList List of Schedule instances to be converted into DTOs.
     * @return ScheduleDTO list
     */
    private static List<ScheduleDTO> scheduleEntitytoDTO(List<Schedule> scheduleList){
        List<ScheduleDTO> schedulesDTO = new ArrayList<>();
        for (Schedule entity: scheduleList){
            schedulesDTO.add(scheduleToDTO(entity));
        }
        return schedulesDTO;
    }

    private List<DoctorUffaPriority> cloneDoctorUffaPriorities(List<DoctorUffaPriority> priorities) {
        List<DoctorUffaPriority> cloned = new ArrayList<>();
        if (priorities == null) {
            return cloned;
        }
        for (DoctorUffaPriority source : priorities) {
            if (source == null) {
                continue;
            }
            DoctorUffaPriority copy = new DoctorUffaPriority();
            copy.setId(source.getId());
            copy.setDoctor(source.getDoctor());
            copy.setSchedule(source.getSchedule());
            copy.setGeneralPriority(source.getGeneralPriority());
            copy.setNightPriority(source.getNightPriority());
            copy.setLongShiftPriority(source.getLongShiftPriority());
            copy.setPartialGeneralPriority(source.getPartialGeneralPriority());
            copy.setPartialNightPriority(source.getPartialNightPriority());
            copy.setPartialLongShiftPriority(source.getPartialLongShiftPriority());
            cloned.add(copy);
        }
        return cloned;
    }

    private List<DoctorUffaPrioritySnapshot> cloneDoctorUffaPrioritySnapshots(List<DoctorUffaPrioritySnapshot> snapshots) {
        List<DoctorUffaPrioritySnapshot> cloned = new ArrayList<>();
        if (snapshots == null) {
            return cloned;
        }
        for (DoctorUffaPrioritySnapshot source : snapshots) {
            if (source == null) {
                continue;
            }
            DoctorUffaPrioritySnapshot copy = new DoctorUffaPrioritySnapshot();
            copy.setId(source.getId());
            copy.setDoctor(source.getDoctor());
            copy.setSchedule(source.getSchedule());
            copy.setGeneralPriority(source.getGeneralPriority());
            copy.setNightPriority(source.getNightPriority());
            copy.setLongShiftPriority(source.getLongShiftPriority());
            cloned.add(copy);
        }
        return cloned;
    }

    /**
     * This private method converts a DTO user into a doctor instance. It supports the work of userDTOtoEntity() method.
     * @param userDTO DTO user to be converted into doctor
     * @return Doctor instance
     */
    private Doctor userDTOtoEntity(UserCreationDTO userDTO) {
        /*
        Seniority seniority = null;
        if(userDTO.getSeniority().equals("STRUCTURED"))
            seniority = Seniority.STRUCTURED;
        else if(userDTO.getSeniority().equals("SPECIALIST_JUNIOR"))
            seniority = Seniority.SPECIALIST_JUNIOR;
        else if (userDTO.getSeniority().equals("SPECIALIST_SENIOR"))
            seniority = Seniority.SPECIALIST_SENIOR;

        Set<SystemActor> systemActors = new HashSet<>();
        for(String sa : userDTO.getSystemActors()) {
            if(sa.equals("CONFIGURATOR"))
                systemActors.add(SystemActor.CONFIGURATOR);
            else if(sa.equals("PLANNER"))
                systemActors.add(SystemActor.PLANNER);
            else if(sa.equals("DOCTOR"))
                systemActors.add(SystemActor.DOCTOR);
        }

        Doctor doctor = new Doctor(userDTO.getId(), userDTO.getName(),userDTO.getLastname(),userDTO.getTaxCode(),userDTO.getBirthday(),userDTO.getEmail(), userDTO.getPassword(), seniority, systemActors);

         */

        Optional<Doctor> doctor = doctorDAO.findById(userDTO.getId());
        return doctor.orElse(null);
    }

    /**
     * This private method converts a list of DTO users a list of doctors.
     * @param usersDTO List of DTO users instances to be converted into doctors.
     * @return Doctor list
     */
    private Set<Doctor> usersDTOtoEntity(Set<UserCreationDTO> usersDTO) {
        Set<Doctor> doctors = new HashSet<>();
        for (UserCreationDTO dto: usersDTO){
            doctors.add(userDTOtoEntity(dto));
        }
        return doctors;
    }

}

