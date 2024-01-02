package org.cswteams.ms3.control.specializations;

import org.cswteams.ms3.control.user.UserController;
import org.cswteams.ms3.dao.SpecializationDAO;
import org.cswteams.ms3.dto.singleDoctorSpecializations.SingleDoctorSpecializationsDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SpecializationsController implements ISpecializationsController{

    @Autowired
    private UserController userController;

    @Autowired
    private SpecializationDAO specializationDAO;

    public SingleDoctorSpecializationsDTO getSingleDoctorSpecializations(Long doctorID){
        //DoctorDTO doctor = userController.getSingleUser(doctorID);
        //Set<String> specializations = Set.copyOf(specializationDAO.getByType(doctorID));

        /*return new SingleDoctorSpecializationsDTO(
                doctor.getId(),
                doctor.getName(),
                doctor.getLastname(),
                specializations
        );*/
        return null;
    }
}
