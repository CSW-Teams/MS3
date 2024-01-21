package org.cswteams.ms3.control.scheduler;

import java.time.LocalDate;
import java.time.ZoneId;
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
import org.cswteams.ms3.enums.Seniority;
import org.cswteams.ms3.enums.SystemActor;
import org.cswteams.ms3.exception.AssegnazioneTurnoException;
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
     * Proxy of the method createSchedule, when the DoctorUffaPriority list cannot be retrieved
     */
    @Override
    @Transactional
    public Schedule createSchedule(LocalDate startDate, LocalDate endDate) {
        List<DoctorUffaPriority> doctorUffaPriorityList = doctorUffaPriorityDAO.findAll();
        return createSchedule(startDate, endDate, doctorUffaPriorityList);
    }


    /**
     * This method creates a new shift schedule by specifying start date and end date.
     * @param startDate First date of the shift schedule
     * @param endDate Last date of the shift schedule
     * @return An instance of schedule if correctly created and saved in persistence, null otherwise
     */
    @Override
    @Transactional
    public Schedule createSchedule(LocalDate startDate, LocalDate endDate, List<DoctorUffaPriority> doctorUffaPriorityList)  {

        //Check if there already exists a shift schedule for the dates we want to plan.
        if(!check(startDate,endDate))
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
                doctorUffaPriorityList          //All the information about priority levels on all the queues of the doctors
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
            System.out.println(e.getMessage());
            return null;
        }

    }

    /**
     * This method recreates an existing shift scheduling. It is not possible to recreate a schedule in the past.
     * @param id An existing schedule ID
     * @return Boolean that represents if the regeneration ended successfully
     */
    @Override
    public boolean recreateSchedule(long id) {
        Optional<Schedule> optionalSchedule = scheduleDAO.findById(id);
        if(optionalSchedule.isEmpty())
            return false;

        Schedule schedule = optionalSchedule.get();
        LocalDate startDate = LocalDate.ofEpochDay(schedule.getStartDate());
        LocalDate endDate = LocalDate.ofEpochDay(schedule.getEndDate());

        List<DoctorUffaPriority> prioritiesSnapshot = schedule.getDoctorUffaPrioritiesSnapshot();

        //It is not allowed to remove a shift schedule in the past.
        if(!removeSchedule(id))
            return false;

        createSchedule(startDate,endDate, prioritiesSnapshot);
        return true;
    }

    /**
     * This method adds a new concrete shift to an existing schedule. In particular, it looks for the schedule containing
     * the date of the new concrete shift and passes it to ScheduleBuilder.
     * @param concreteShift The new concrete shift to be added to the schedule
     * @param forced If true, the concrete shift will be added if it respects all the non-violable constraints;
     *               if false, the concrete shift will be added only if it respects all the existing constraints.
     * @return An instance of the updated shift schedule
     * @throws IllegalScheduleException Rised if the new concrete shift makes the schedule illegal
     */
    public Schedule addConcreteShift(ConcreteShift concreteShift, boolean forced) throws IllegalScheduleException {

        Schedule schedule;

        //We create a new builder passing him as parameter an existing shift schedule.
        this.scheduleBuilder = new ScheduleBuilder(
                constraintDAO.findAll(),            //All the constraints that shall be respected when a doctor is assigned to a concrete shift
                doctorUffaPriorityDAO.findAll(),    //All the possible doctors that can be assigned to the concrete shifts
                scheduleDAO.findByDateBetween(concreteShift.getDate())  //Existing shift schedule
        );

        schedule = this.scheduleBuilder.addConcreteShift(concreteShift,forced);

        //We commit changes to schedule only if they do not taint it
        if (schedule.getCauseIllegal() == null){
            scheduleDAO.flush();
        }
        return schedule;

    }

    /**
     * This method removes a concrete shift from a schedule but not from the database.
     * @param concreteShiftOld Concrete shift to be removed
     */
    public void removeConcreteShiftFromSchedule(ConcreteShift concreteShiftOld) {
        Schedule schedule = scheduleDAO.findByDateBetween(concreteShiftOld.getDate());
        schedule.getConcreteShifts().remove(concreteShiftOld);
        scheduleDAO.flush();
    }

    /**
     * This method removes a concrete shift from the database.
     * @param concreteShiftId ID of the concrete shift to be removed
     * @return Boolean that represents if the deletion was successful
     */
    @Override
    public boolean removeConcreteShift(Long concreteShiftId) {
       Optional<ConcreteShift> concreteShift = concreteShiftDAO.findById(concreteShiftId);
        if(concreteShift.isEmpty())
            return false;

        this.removeConcreteShiftFromSchedule(concreteShift.get());
        concreteShiftDAO.delete(concreteShift.get());
        return true;
    }

    /**
     * This method adds a new concrete shift to an existing schedule; the concrete shift is described by the DTO parameter.
     * @param registerConcreteShiftDTO DTO class that describes the new concrete shift
     * @param forced If true, the concrete shift will be added if it respects all the non-violable constraints;
     *               if false, the concrete shift will be added only if it respects all the existing constraints.
     * @return An instance of the updated shift schedule
     * @throws AssegnazioneTurnoException Rised if the DTO parameter describes a non-existing concrete shift
     * @throws IllegalScheduleException Rised if the new concrete shift makes the schedule illegal
     */
    @Override
    public Schedule addConcreteShift(RegisterConcreteShiftDTO registerConcreteShiftDTO, boolean forced) throws AssegnazioneTurnoException, IllegalScheduleException {

        //We need a shift which is present in the database in order to convert the DTO into an entity.
        List<Shift> shiftsList = shiftDAO.findAllByMedicalServiceLabelAndTimeSlot(registerConcreteShiftDTO.getServizio().getNome(), registerConcreteShiftDTO.getTimeSlot());
        if(shiftsList.isEmpty())
            throw new AssegnazioneTurnoException("Non esiste uno shift coi servizi specificati.");
        Shift shift = null;
        for(Shift shiftDB: shiftsList){
            //if(shiftDB.getMansione().equals(registerConcreteShiftDTO.getMansione())){
            if(shiftDB.getMedicalService().equals(registerConcreteShiftDTO.getServices())) {
                shift = shiftDB;
                break;
            }
        }
        if(shift == null){
            throw new AssegnazioneTurnoException("Non esiste uno shift coi servizi specificati.");
        }

        ConcreteShift concreteShift = new ConcreteShift(
                LocalDate.of(registerConcreteShiftDTO.getYear(), registerConcreteShiftDTO.getMonth(), registerConcreteShiftDTO.getDay()).atStartOfDay(ZoneId.systemDefault()).toEpochSecond()*1000,
                shift
        );
        //definition of doctorAssignmentList to set into concreteShift
        for(Doctor onCallDoctor : usersDTOtoEntity(registerConcreteShiftDTO.getOnCallDoctors())) {
            concreteShift.getDoctorAssignmentList().add(new DoctorAssignment(onCallDoctor, ConcreteShiftDoctorStatus.ON_CALL, concreteShift, null));   //TODO: define the TASK.
        }
        for(Doctor onDutyDoctor : usersDTOtoEntity(registerConcreteShiftDTO.getOnDutyDoctors())) {
            concreteShift.getDoctorAssignmentList().add(new DoctorAssignment(onDutyDoctor, ConcreteShiftDoctorStatus.ON_DUTY, concreteShift, null));   //TODO: define the TASK.
        }

        if(!checkDoctorsOnConcreteShift(concreteShift)){
            throw new AssegnazioneTurnoException("Collisione tra utenti reperibili e di guardia");
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

    /**
     * This method modifies an existing concrete shift. In particular, it removes the existing concrete shifts, checks if
     * the new version of the concrete shift respects all the constraints and, if the checks succeed, then saves the new
     * version of the concrete shift into the database. Instead, if there are some violated constraints, then the old
     * version of the concrete shift is saved into the database again.
     * @param modifyConcreteShiftDTO DTO instance representing the changes to make in an existing concrete shift
     * @return An instance of the updated shift schedule containing the modified concrete shift
     * @throws IllegalScheduleException Rised if the new concrete shift makes the schedule illegal
     */
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
                for (DoctorAssignment da : concreteShiftNew.getDoctorAssignmentList()) {
                    if(da.getConcreteShiftDoctorStatus() == ConcreteShiftDoctorStatus.ON_DUTY)
                        concreteShiftNew.getDoctorAssignmentList().remove(da);

                }
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
                for (DoctorAssignment da : concreteShiftNew.getDoctorAssignmentList()) {
                    if(da.getConcreteShiftDoctorStatus() == ConcreteShiftDoctorStatus.ON_CALL)
                        concreteShiftNew.getDoctorAssignmentList().remove(da);

                }
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

    /**
     * This method retrieves all the existing schedules from the database.
     * @return List of DTO instances representing all the existing schedules to be delivered to the frontend
     */
    public List<ScheduleDTO> readSchedules(){
        return scheduleEntitytoDTO(scheduleDAO.findAll());
    }

    /**
     * This method retrieves the illegal schedules from the database.
     * @return List of DTO instances representing the illegal schedules to be delivered to the frontend
     */
    @Override
    public List<ScheduleDTO> readIllegalSchedules() {
        return scheduleEntitytoDTO(scheduleDAO.leggiSchedulazioniIllegali());
    }

    /**
     * This method removes an existing shift schedule. This operation can be performed correctly only if the schedule is
     * in the future and not in the past.
     * @param id ID of the shift schedule to be removed
     * @return Boolean that represents if the deletion was successful
     */
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
    public boolean check(LocalDate startNewSchedule, LocalDate endNewSchedule){
        List<Schedule> allSchedule = scheduleDAO.findAll();

        for (Schedule schedule : allSchedule) {
            if (!(LocalDate.ofEpochDay(schedule.getStartDate())).isBefore(endNewSchedule) && !(LocalDate.ofEpochDay(schedule.getEndDate())).isBefore(startNewSchedule))
                return false;

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
    private static Doctor userDTOtoEntity(UserCreationDTO userDTO) {
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

        return new Doctor(userDTO.getName(),userDTO.getLastname(),userDTO.getTaxCode(),userDTO.getBirthday(),userDTO.getEmail(), userDTO.getPassword(), seniority, systemActors);
    }

    /**
     * This private method converts a list of DTO users a list of doctors.
     * @param usersDTO List of DTO users instances to be converted into doctors.
     * @return Doctor list
     */
    private static Set<Doctor> usersDTOtoEntity(Set<UserCreationDTO> usersDTO) {
        Set<Doctor> doctors = new HashSet<>();
        for (UserCreationDTO dto: usersDTO){
            doctors.add(userDTOtoEntity(dto));
        }
        return doctors;
    }

}
