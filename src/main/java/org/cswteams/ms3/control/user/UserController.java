package org.cswteams.ms3.control.user;

import org.cswteams.ms3.control.utils.MappaUtenti;
import org.cswteams.ms3.dao.DoctorDAO;
import org.cswteams.ms3.dao.UserDAO;
import org.cswteams.ms3.dto.DoctorDTO;
import org.cswteams.ms3.entity.Doctor;
import org.cswteams.ms3.entity.Preference;
import org.cswteams.ms3.entity.Specialization;
import org.cswteams.ms3.entity.User;
import org.cswteams.ms3.entity.condition.Condition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class UserController implements IUserController {

    @Autowired
    private DoctorDAO doctorDAO;

    @Autowired
    private UserDAO userDAO;

    @Override
    public Set<DoctorDTO> getAllUsers() {
        List<User> userList = userDAO.findAll();
        Set<DoctorDTO> doctorDTOSet = new HashSet<>();
        for (User user: userList){
            doctorDTOS.add(utenteEntityToDTO(user));
        }

        return doctorDTOSet;
    }

    @Override
    public Object createUser(DoctorDTO s) {
        return doctorDAO.save(MappaUtenti.utenteDTOtoEntity(s));
    }

    public DoctorDTO getSingleUser(long idUtente) {
        Doctor doctor = doctorDAO.findById(idUtente);
        return MappaUtenti.utenteEntityToDTO(doctor);
    }

    /**
     * TODO: Refactor this usage when design pattern will be implemented
     * TODO: Add checks on persistence state and throw exception in that case
     * TODO: Check if condition is still valid on user login to implements temporary condition deleting logic
     * @param doctor
     * @param condition
     * @throws Exception
     */
    public void addCondition(Doctor doctor, Condition condition) throws Exception {
        doctor.addCondition(condition);
    }

    public void addPreference(Doctor doctor, Preference preference) throws Exception {
        doctor.addPreference(preference);
    }

    public void addSpecialization(Doctor doctor, Specialization specialization) throws Exception {
        doctor.addSpecialization(specialization);
    }

}
