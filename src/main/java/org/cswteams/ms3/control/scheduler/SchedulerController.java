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
import org.cswteams.ms3.entity.scocciature.Scocciatura;
import org.cswteams.ms3.enums.ConcreteShiftDoctorStatus;
import org.cswteams.ms3.exception.ConcreteShiftException;
import org.cswteams.ms3.exception.IllegalScheduleException;
import org.cswteams.ms3.utils.DateConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

// TODO: Generate concrete shift controller from this class
@Service
public class SchedulerController implements ISchedulerController {

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


    @Override
    @Transactional
    public Schedule createSchedule(LocalDate startDate, LocalDate endDate) {
        List<DoctorUffaPriority> doctorUffaPriorityList = doctorUffaPriorityDAO.findAll();
        List<DoctorUffaPrioritySnapshot> doctorUffaPrioritySnapshot = doctorUffaPrioritySnapshotDAO.findAll();
        return createSchedule(startDate, endDate, doctorUffaPriorityList, doctorUffaPrioritySnapshot);
    }


    @Override
    @Transactional
    public Schedule createSchedule(LocalDate startDate, LocalDate endDate, List<DoctorUffaPriority> doctorUffaPriorityList, List<DoctorUffaPrioritySnapshot> snapshot)  {

        boolean hasExistingSchedules = !scheduleDAO.findAll().isEmpty();
        if (!hasExistingSchedules && startDate.isBefore(LocalDate.now())) {
            return null; // non consentire schedulazioni iniziali nel passato
        }

        //Check if there already exists a shift schedule for the dates we want to plan.
        if(!alreadyExistsAnotherSchedule(startDate,endDate))
            return null;

        //currentDay = date used to iterate on the dates interval (start date -> end date)
        LocalDate currentDay = startDate;
        List<ConcreteShift> allConcreteShifts = new ArrayList<>();

        //Creation of the concrete shifts by associating a date foreach shift
        //Iteration on all the dates of the interval. Foreach date, we iterate on all the shifts.
        while(!currentDay.isAfter(endDate)){
            for(Shift shift : shiftDAO.findAll()){

                //We can assign this shift to a certain date only if the corresponding day of week is admissible for the shift.
                if (shift.getDaysOfWeek().contains(currentDay.getDayOfWeek())){
                    allConcreteShifts.add(new ConcreteShift(currentDay.toEpochDay(), shift));
                }

            }
            //We move on the next day.
            currentDay = currentDay.plusDays(1);
        }

        //Creation of a schedule builder foreach new shift schedule
        try {
            //NB: DO NOT move this line
            List<Scocciatura> scocciaturaList = scocciaturaDAO.findAll();

            this.scheduleBuilder = new ScheduleBuilder(
                startDate,                      //Start date of the shift schedule
                endDate,                        //End date of the shift schedule
                constraintDAO.findAll(),        //All the constraints to respect when a doctor is assigned to a concrete shift
                allConcreteShifts,              //Concrete shifts (without doctors)
                doctorDAO.findAll(),            //All the possible doctors who can be assigned to the concrete shifts
                holidayDAO.findAll(),           //All the holidays saved in the db
                doctorHolidaysDAO.findAll(),    //All the associations between doctors and holidays
                doctorUffaPriorityList,         //All the information about priority levels on all the queues of the doctors
                snapshot                        //Snapshot to update to save actual priorities
                );

            ControllerScocciatura controllerScocciatura = new ControllerScocciatura(scocciaturaList);
            //We set the controller that manages doctors priorities.
            this.scheduleBuilder.setControllerScocciatura(controllerScocciatura);

            Schedule schedule = this.scheduleBuilder.build();

            scheduleDAO.save(schedule);
            for(DoctorUffaPriority dup: schedule.getDoctorUffaPriorityList()) {
                dup.setSchedule(schedule);
                doctorUffaPriorityDAO.save(dup);
            }

            return schedule;

        } catch (IllegalScheduleException e) {
            return null;
        }

    }

    @Override
    public boolean recreateSchedule(long id) {
        Optional<Schedule> optionalSchedule = scheduleDAO.findById(id);
        if(optionalSchedule.isEmpty())
            return false;

        Schedule schedule = optionalSchedule.get();
        LocalDate startDate = LocalDate.ofEpochDay(schedule.getStartDate());
        LocalDate endDate = LocalDate.ofEpochDay(schedule.getEndDate());

        List<DoctorUffaPrioritySnapshot> doctorUffaPrioritySnapshot = doctorUffaPrioritySnapshotDAO.findAll();
        List<DoctorUffaPriority> doctorUffaPriorityList = doctorUffaPriorityDAO.findAll();

        /* Restore priorities to snapshot */
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

        //It is not allowed to remove a shift schedule in the past.
        if(!removeSchedule(id))
            return false;

        createSchedule(startDate,endDate, doctorUffaPriorityList, doctorUffaPrioritySnapshot);
        return true;
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

            // block only exact duplicate interval; allow overlaps/adjacent ranges
            if (existingStart.equals(startNewSchedule) && existingEnd.equals(endNewSchedule)) {
                return false;
            }
        }
        return true;

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


