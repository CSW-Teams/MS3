package org.cswteams.ms3.control.user;

import org.cswteams.ms3.dao.DoctorDAO;
import org.cswteams.ms3.dao.UserDAO;
import org.cswteams.ms3.dto.user.UserCreationDTO;
import org.cswteams.ms3.dto.user.UserDTO;
import org.cswteams.ms3.dto.user.UserDetailsDTO;
import org.cswteams.ms3.entity.Doctor;
import org.cswteams.ms3.entity.Preference;
import org.cswteams.ms3.entity.Specialization;
import org.cswteams.ms3.entity.User;
import org.cswteams.ms3.entity.condition.Condition;
import org.cswteams.ms3.enums.SystemActor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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
    public Set<UserDTO> getAllUsers() {
        List<User> users = userDAO.findAll();
        Set<UserDTO> doctorsSet = new HashSet<>();

        for (User u: users){
            List<String> systemActors = new ArrayList<>();
            for(SystemActor a : u.getSystemActors()){
                systemActors.add(a.toString());
            }

            UserDTO dto = new UserDTO(u.getId(), u.getName(), u.getLastname(), u.getBirthday(), systemActors);
            doctorsSet.add(dto);
        }

        return doctorsSet;
    }

    @Override
    public void createUser(UserCreationDTO s) {

        List<SystemActor> enumList = new ArrayList<>();

        for (String str : s.getSystemActors()) {
            try {
                SystemActor enumValue = SystemActor.valueOf(str);
                enumList.add(enumValue);
            } catch (IllegalArgumentException e) {
                System.out.println("String '" + str + "' does not match any enum constant.");
            }
        }

        User newUser = new User(s.getName(), s.getLastname(), s.getTaxCode(),
                s.getBirthday(), s.getEmail(), s.getPassword(), enumList);
        userDAO.save(newUser);
    }

    public UserDetailsDTO getSingleUser(long userId) {
        Doctor doctor = doctorDAO.findById(userId);
        return new UserDetailsDTO(doctor.getName(), doctor.getLastname(), doctor.getEmail(), doctor.getBirthday(), doctor.getSeniority().toString());
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
