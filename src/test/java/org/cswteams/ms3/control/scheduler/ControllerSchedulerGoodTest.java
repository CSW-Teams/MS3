package org.cswteams.ms3.control.scheduler;

import org.cswteams.ms3.control.medicalService.MedicalServiceController;
import org.cswteams.ms3.control.user.UserController;
import org.cswteams.ms3.control.vincoli.IConstraintController;
import org.cswteams.ms3.dao.DoctorDAO;
import org.cswteams.ms3.dao.SpecializationDAO;
import org.cswteams.ms3.entity.Doctor;
import org.cswteams.ms3.entity.MedicalService;
import org.cswteams.ms3.entity.Specialization;
import org.cswteams.ms3.entity.Task;
import org.cswteams.ms3.enums.Seniority;
import org.cswteams.ms3.enums.SystemActor;
import org.cswteams.ms3.enums.TaskEnum;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Set;

import static org.junit.Assert.fail;

public class ControllerSchedulerGoodTest extends ControllerSchedulerTest {

    @Autowired
    private IConstraintController constraintController;

    @Autowired
    private SpecializationDAO specializationDAO ;

    @Autowired
    private MedicalServiceController medicalServiceControllercontroller ;

    @Autowired
    private UserController userController ;

    @Autowired
    private DoctorDAO doctorDAO ;

    @Override
    public void populateDB() {

        //Specializations
        Specialization a_logia = new Specialization("Alogia") ;
        Specialization b_logia = new Specialization("Blogia") ;

        specializationDAO.save(a_logia) ;
        specializationDAO.save(b_logia) ;

        //Tasks and services

        Task ward = new Task(TaskEnum.WARD);
        Task clinic = new Task(TaskEnum.CLINIC);
        Task emergency = new Task(TaskEnum.EMERGENCY);
        Task operatingRoom = new Task(TaskEnum.OPERATING_ROOM);

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


    }
}
