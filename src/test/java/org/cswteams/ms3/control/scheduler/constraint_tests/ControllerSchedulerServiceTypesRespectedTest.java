package org.cswteams.ms3.control.scheduler.constraint_tests;

import org.cswteams.ms3.control.medicalService.MedicalServiceController;
import org.cswteams.ms3.control.user.UserController;
import org.cswteams.ms3.dao.*;
import org.cswteams.ms3.entity.*;
import org.cswteams.ms3.entity.condition.PermanentCondition;
import org.cswteams.ms3.entity.condition.TemporaryCondition;
import org.cswteams.ms3.enums.Seniority;
import org.cswteams.ms3.enums.SystemActor;
import org.cswteams.ms3.enums.TaskEnum;
import org.cswteams.ms3.enums.TimeSlot;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

import static org.junit.Assert.fail;

public class ControllerSchedulerServiceTypesRespectedTest extends ControllerSchedulerExtraTest {

    @Autowired
    private SpecializationDAO specializationDAO ;

    @Autowired
    private MedicalServiceController medicalServiceControllercontroller ;

    @Autowired
    private UserController userController ;

    @Autowired
    private DoctorDAO doctorDAO ;

    @Autowired
    private ShiftDAO shiftDAO ;

    @Autowired
    private TaskDAO taskDAO ;

    @Autowired
    private HolidayDAO holidayDAO ;

    @Autowired
    private DoctorUffaPriorityDAO doctorUffaPriorityDAO ;

    @Autowired
    private DoctorHolidaysDAO doctorHolidaysDAO ;

    @Autowired
    private DoctorUffaPrioritySnapshotDAO doctorUffaPrioritySnapshotDAO ;

    @Autowired
    private PermanentConditionDAO permanentConditionDAO ;

    @Autowired
    private TemporaryConditionDAO temporaryConditionDAO ;

    @Override
    public void populateDB() {

        //CREA LE CATEGORIE DI TIPO STATO (ESCLUSIVE PER I TURNI)
        // Condition may be structure specific TODO: Ask if it is needed a configuration file for that
        PermanentCondition over62 = new PermanentCondition("OVER 62");
        TemporaryCondition pregnant = new TemporaryCondition("INCINTA", LocalDate.now().toEpochDay(), LocalDate.now().plusMonths(9).toEpochDay());
        TemporaryCondition maternity = new TemporaryCondition("IN MATERNITA'", LocalDate.now().toEpochDay(), LocalDate.now().plusDays(60).toEpochDay());
        TemporaryCondition vacation = new TemporaryCondition("IN FERIE", LocalDate.now().toEpochDay(), LocalDate.now().plusDays(7).toEpochDay());
        TemporaryCondition sick = new TemporaryCondition("IN MALATTIA", LocalDate.now().toEpochDay(), LocalDate.now().plusDays(7).toEpochDay());

        permanentConditionDAO.saveAndFlush(over62) ;
        temporaryConditionDAO.saveAndFlush(pregnant) ;
        temporaryConditionDAO.saveAndFlush(maternity) ;
        temporaryConditionDAO.saveAndFlush(vacation) ;
        temporaryConditionDAO.saveAndFlush(sick) ;

        //Specializations
        Specialization a_logia = new Specialization("ALOGIA") ;

        specializationDAO.save(a_logia) ;

        //Tasks and services

        Task ward = new Task(TaskEnum.WARD) ;
        Task clinic = new Task(TaskEnum.CLINIC) ;
        taskDAO.saveAndFlush(ward) ;
        taskDAO.saveAndFlush(clinic) ;

        MedicalService repartoAlogia = medicalServiceControllercontroller.createService(List.of(ward, clinic), "ALOGIA") ;

        //Doctors

        Doctor doc1 = new Doctor("Esperto", "Alogia1", "SLVMTN97T56H501Y", LocalDate.of(1997, 3, 14), "espertoalogia@gmail.com", "passw", Seniority.STRUCTURED, Set.of(SystemActor.CONFIGURATOR));
        Doctor doc2 = new Doctor("Esperto", "Alogia2", "SLVMTN97T56H501Y", LocalDate.of(1997, 3, 14), "espertoblogia97@gmail.com", "passw", Seniority.STRUCTURED, Set.of(SystemActor.CONFIGURATOR));
        Doctor doc3 = new Doctor("Esperto", "Alogia3", "SLVMTN97T56H501Y", LocalDate.of(1997, 3, 14), "espertoalogia@gmail.com", "passw", Seniority.STRUCTURED, Set.of(SystemActor.CONFIGURATOR));
        Doctor doc4 = new Doctor("Esperto", "Alogia4", "SLVMTN97T56H501Y", LocalDate.of(1997, 3, 14), "espertoblogia97@gmail.com", "passw", Seniority.SPECIALIST_SENIOR, Set.of(SystemActor.CONFIGURATOR));
        Doctor doc5 = new Doctor("Esperto", "Alogia5", "SLVMTN97T56H501Y", LocalDate.of(1997, 3, 14), "espertoalogia@gmail.com", "passw", Seniority.STRUCTURED, Set.of(SystemActor.CONFIGURATOR));
        Doctor doc6 = new Doctor("Esperto", "Alogia6", "SLVMTN97T56H501Y", LocalDate.of(1997, 3, 14), "espertoblogia97@gmail.com", "passw", Seniority.SPECIALIST_SENIOR, Set.of(SystemActor.CONFIGURATOR));

        try {
            userController.addSpecialization(doc1, a_logia) ;
            userController.addSpecialization(doc2, a_logia) ;
            userController.addSpecialization(doc3, a_logia) ;
            userController.addSpecialization(doc4, a_logia) ;
            userController.addSpecialization(doc5, a_logia) ;
            userController.addSpecialization(doc6, a_logia) ;
        } catch (Exception e) {
            fail() ;
        }

        doctorDAO.save(doc1) ;
        doctorDAO.save(doc2) ;
        doctorDAO.save(doc3) ;
        doctorDAO.save(doc4) ;
        doctorDAO.save(doc5) ;
        doctorDAO.save(doc6) ;

        Map<Seniority, Integer> alogiaClinicQuantities = new HashMap<>() ;
        alogiaClinicQuantities.put(Seniority.STRUCTURED, 1) ;
        QuantityShiftSeniority repartoAlogiaQssClinic = new QuantityShiftSeniority(alogiaClinicQuantities, clinic) ; //Service only has ward, only Qss is clinic

        Map<Seniority, Integer> alogiaWardQuantities = new HashMap<>() ;
        alogiaWardQuantities.put(Seniority.SPECIALIST_SENIOR, 1) ;
        alogiaWardQuantities.put(Seniority.STRUCTURED, 1) ;
        QuantityShiftSeniority repartoAlogiaQssWard = new QuantityShiftSeniority(alogiaWardQuantities, ward) ; //Service only has ward, only Qss is clinic

        Set<DayOfWeek> monday = new HashSet<>(Collections.singletonList(DayOfWeek.MONDAY)) ;

        Shift shift1 = new Shift(LocalTime.of(8, 0),
                Duration.ofHours(6),
                repartoAlogia,
                TimeSlot.MORNING,
                List.of(repartoAlogiaQssClinic, repartoAlogiaQssWard),
                monday,
                Collections.emptyList());
        shiftDAO.saveAndFlush(shift1);

        List<Holiday> holidays = holidayDAO.findAll();  //retrieve of holiday entities (and not DTOs)

        //we are assuming that, at the moment of instantiation of DoctorHolidays, the corresponding doctor has worked in no concrete shift in the past.
        HashMap<Holiday, Boolean> holidayMap = new HashMap<>();
        for (Holiday holiday : holidays) {
            if (!holiday.getName().equals("Domenica"))   //we do not care about Sundays as holidays
                holidayMap.put(holiday, false);

        }

        DoctorUffaPriority dup = new DoctorUffaPriority(doc1);
        DoctorUffaPrioritySnapshot doc1UffaPrioritySnapshot = new DoctorUffaPrioritySnapshot(doc1);
        DoctorHolidays dh = new DoctorHolidays(doc1, holidayMap);

        doctorUffaPriorityDAO.save(dup);
        doctorHolidaysDAO.save(dh);
        doctorUffaPrioritySnapshotDAO.save(doc1UffaPrioritySnapshot);

        DoctorUffaPriority dup2 = new DoctorUffaPriority(doc2);
        DoctorUffaPrioritySnapshot doc2UffaPrioritySnapshot = new DoctorUffaPrioritySnapshot(doc2);
        DoctorHolidays dh2 = new DoctorHolidays(doc2, holidayMap);

        doctorUffaPriorityDAO.save(dup2);
        doctorHolidaysDAO.save(dh2);
        doctorUffaPrioritySnapshotDAO.save(doc2UffaPrioritySnapshot);

        DoctorUffaPriority dup3 = new DoctorUffaPriority(doc3);
        DoctorUffaPrioritySnapshot doc3UffaPrioritySnapshot = new DoctorUffaPrioritySnapshot(doc3);
        DoctorHolidays dh3 = new DoctorHolidays(doc3, holidayMap);

        doctorUffaPriorityDAO.save(dup3);
        doctorHolidaysDAO.save(dh3);
        doctorUffaPrioritySnapshotDAO.save(doc3UffaPrioritySnapshot);

        DoctorUffaPriority dup4 = new DoctorUffaPriority(doc4);
        DoctorUffaPrioritySnapshot doc4UffaPrioritySnapshot = new DoctorUffaPrioritySnapshot(doc4);
        DoctorHolidays dh4 = new DoctorHolidays(doc4, holidayMap);

        doctorUffaPriorityDAO.save(dup4);
        doctorHolidaysDAO.save(dh4);
        doctorUffaPrioritySnapshotDAO.save(doc4UffaPrioritySnapshot);

        DoctorUffaPriority dup5 = new DoctorUffaPriority(doc5);
        DoctorUffaPrioritySnapshot doc5UffaPrioritySnapshot = new DoctorUffaPrioritySnapshot(doc5);
        DoctorHolidays dh5 = new DoctorHolidays(doc5, holidayMap);

        doctorUffaPriorityDAO.save(dup5);
        doctorHolidaysDAO.save(dh5);
        doctorUffaPrioritySnapshotDAO.save(doc5UffaPrioritySnapshot);

        DoctorUffaPriority dup6 = new DoctorUffaPriority(doc6);
        DoctorUffaPrioritySnapshot doc6UffaPrioritySnapshot = new DoctorUffaPrioritySnapshot(doc6);
        DoctorHolidays dh6 = new DoctorHolidays(doc6, holidayMap);

        doctorUffaPriorityDAO.save(dup6);
        doctorHolidaysDAO.save(dh6);
        doctorUffaPrioritySnapshotDAO.save(doc6UffaPrioritySnapshot);

        //Set all parameters in parent class, like in @Parametrized

        super.isPossible = true;
        super.start = LocalDate.of(2025, 4, 1);
        super.end = LocalDate.of(2025, 4, 2);

    }

    @Override
    public void extraChecks() {

    }
}
