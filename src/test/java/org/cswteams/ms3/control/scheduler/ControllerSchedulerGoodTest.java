package org.cswteams.ms3.control.scheduler;

import org.cswteams.ms3.control.medicalService.MedicalServiceController;
import org.cswteams.ms3.control.user.UserController;
import org.cswteams.ms3.control.vincoli.IConstraintController;
import org.cswteams.ms3.dao.DoctorDAO;
import org.cswteams.ms3.dao.ShiftDAO;
import org.cswteams.ms3.dao.SpecializationDAO;
import org.cswteams.ms3.dao.TaskDAO;
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

    @Override
    public void populateDB() {

        //Specializations
        Specialization a_logia = new Specialization("Alogia") ;
        Specialization b_logia = new Specialization("Blogia") ;

        specializationDAO.save(a_logia) ;
        specializationDAO.save(b_logia) ;

        //Tasks and services

        Task ward = new Task(TaskEnum.WARD) ;
        taskDAO.saveAndFlush(ward) ;

        MedicalService repartoAlogia = medicalServiceControllercontroller.createService(Collections.singletonList(ward), "REPARTO ALOGIA") ;
        MedicalService repartoBlogia = medicalServiceControllercontroller.createService(Collections.singletonList(ward), "REPARTO BLOGIA") ;

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

        //Set all parameters in parent class, like in @Parametrized

        super.isPossible = true ;
        super.start = LocalDate.of(2024, 3, 1) ;
        super.end = LocalDate.of(2024, 3, 31) ;

    }
}
