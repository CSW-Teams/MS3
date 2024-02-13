package org.cswteams.ms3.control.scheduler.constraint_tests;

import org.cswteams.ms3.control.medicalService.MedicalServiceController;
import org.cswteams.ms3.control.scheduler.constraint_tests.ControllerSchedulerTest;
import org.cswteams.ms3.control.user.UserController;
import org.cswteams.ms3.dao.*;
import org.cswteams.ms3.entity.*;
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

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

public class ControllerSchedulerGoodTest extends ControllerSchedulerTest {

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

    @Override
    public void populateDB() {

        //Specializations
        Specialization a_logia = new Specialization("ALOGIA") ;
        Specialization b_logia = new Specialization("BLOGIA") ;

        specializationDAO.save(a_logia) ;
        specializationDAO.save(b_logia) ;

        //Tasks and services

        Task ward = new Task(TaskEnum.WARD) ;
        taskDAO.saveAndFlush(ward) ;

        MedicalService repartoAlogia = medicalServiceControllercontroller.createService(Collections.singletonList(ward), "ALOGIA") ;
        MedicalService repartoBlogia = medicalServiceControllercontroller.createService(Collections.singletonList(ward), "BLOGIA") ;

        //Doctors

        Doctor doc1 = new Doctor("Esperto", "Alogia", "SLVMTN97T56H501Y", LocalDate.of(1997, 3, 14), "espertoalogia@gmail.com", "passw", Seniority.STRUCTURED, Set.of(SystemActor.CONFIGURATOR));
        Doctor doc2 = new Doctor("Esperto", "Blogia", "SLVMTN97T56H501Y", LocalDate.of(1997, 3, 14), "espertoblogia97@gmail.com", "passw", Seniority.SPECIALIST_SENIOR, Set.of(SystemActor.CONFIGURATOR));

        try {
            userController.addSpecialization(doc1, a_logia) ;
            userController.addSpecialization(doc2, b_logia) ;
        } catch (Exception e) {
            fail() ;
        }

        doctorDAO.save(doc1) ;
        doctorDAO.save(doc2) ;

        Map<Seniority, Integer> alogiaQuantities = new HashMap<>() ;
        alogiaQuantities.put(Seniority.STRUCTURED, 1) ;
        QuantityShiftSeniority repartoAlogiaQss = new QuantityShiftSeniority(alogiaQuantities, ward) ;

        Map<Seniority, Integer> blogiaQuantities = new HashMap<>() ;
        alogiaQuantities.put(Seniority.SPECIALIST_SENIOR, 1) ;
        QuantityShiftSeniority repartoBlogiaQss = new QuantityShiftSeniority(blogiaQuantities, ward) ;

        Set<DayOfWeek> monday = new HashSet<>(Collections.singletonList(DayOfWeek.MONDAY)) ;

        Shift shift1 = new Shift(LocalTime.of(8, 0),
                Duration.ofHours(6),
                repartoAlogia,
                TimeSlot.MORNING,
                Collections.singletonList(repartoAlogiaQss),
                monday,
                Collections.emptyList());
        shiftDAO.saveAndFlush(shift1);

        Shift shift2 = new Shift(LocalTime.of(8, 0),
                Duration.ofHours(6),
                repartoBlogia,
                TimeSlot.MORNING,
                Collections.singletonList(repartoBlogiaQss),
                monday,
                Collections.emptyList());
        shiftDAO.saveAndFlush(shift2);

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

        //Set all parameters in parent class, like in @Parametrized

        super.isPossible = true ;
        super.start = LocalDate.of(2024, 3, 1) ;
        super.end = LocalDate.of(2024, 3, 31) ;

    }
}
