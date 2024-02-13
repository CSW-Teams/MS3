package org.cswteams.ms3.control.user;

import org.cswteams.ms3.dao.DoctorDAO;
import org.cswteams.ms3.dao.UserDAO;
import org.cswteams.ms3.dto.user.UserCreationDTO;
import org.cswteams.ms3.dto.user.UserDTO;
import org.cswteams.ms3.dto.user.UserDetailsDTO;
import org.cswteams.ms3.dto.userprofile.SingleUserProfileDTO;
import org.cswteams.ms3.dto.userprofile.TemporaryConditionDTO;
import org.cswteams.ms3.entity.Doctor;
import org.cswteams.ms3.entity.Preference;
import org.cswteams.ms3.entity.Specialization;
import org.cswteams.ms3.entity.User;
import org.cswteams.ms3.entity.condition.Condition;
import org.cswteams.ms3.entity.condition.PermanentCondition;
import org.cswteams.ms3.entity.condition.TemporaryCondition;
import org.cswteams.ms3.enums.SystemActor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
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

        Set<SystemActor> enumSet = new HashSet<>();

        for (String str : s.getSystemActors()) {
            try {
                SystemActor enumValue = SystemActor.valueOf(str);
                enumSet.add(enumValue);
            } catch (IllegalArgumentException e) {
                System.out.println("String '" + str + "' does not match any enum constant.");
            }
        }

        User newUser = new User(s.getName(), s.getLastname(), s.getTaxCode(),
                s.getBirthday(), s.getEmail(), s.getPassword(), enumSet);
        userDAO.save(newUser);
    }

    @Override
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

    @Override
    public SingleUserProfileDTO getSingleUserProfileInfos(Long userId){
        try{
            Doctor doctor = doctorDAO.findById((long)userId);
            List<String> specializations = new ArrayList<>();
            List<String> systemActors = new ArrayList<>();
            List<String> permanentConditions = new ArrayList<>();
            List<TemporaryConditionDTO> temporaryConditions = new ArrayList<>();

            // Convert specialization entity in string for the frontend
            for(Specialization specialization : doctor.getSpecializations()){
                specializations.add(specialization.getType());
            }

            // Convert systemActor entity in string for the frontend
            for(SystemActor systemActor : doctor.getSystemActors()){
                systemActors.add(systemActor.name());
            }

            // Convert permanent conditions entity in string for the frontend
            for(PermanentCondition permanentCondition : doctor.getPermanentConditions()){
                permanentConditions.add(permanentCondition.getType());
            }

            // Convert temporary conditions entity in string for the frontend
            for(TemporaryCondition temporaryCondition : doctor.getTemporaryConditions()){
                temporaryConditions.add(new TemporaryConditionDTO(
                        temporaryCondition.getType(),
                        temporaryCondition.getStartDate(),
                        temporaryCondition.getEndDate()
                ));
            }


            return new SingleUserProfileDTO(
                    doctor.getId(),
                    doctor.getName(),
                    doctor.getLastname(),
                    doctor.getEmail(),
                    doctor.getBirthday().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")),
                    doctor.getSeniority().name(),
                    specializations,
                    systemActors,
                    permanentConditions,
                    temporaryConditions);
        }catch (NullPointerException nullPointerException){
            return new SingleUserProfileDTO(
                    -1L,
                    "",
                    "",
                    "",
                    "",
                    "",
                    new ArrayList<>(),
                    new ArrayList<>(),
                    new ArrayList<>(),
                    new ArrayList<>());
        }catch (Exception e){
            return null;
        }

    }

    @Override
    public void deleteUserSystemActor(Long userID, String systemActor) {
        User user = userDAO.findById((long) userID);
        user.getSystemActors().remove(SystemActor.valueOf(systemActor));
        userDAO.saveAndFlush(user);
    }

    @Override
    public void addSystemActor(Long userID, Set<String> systemActors) {
        User user = userDAO.findById((long) userID);
        for(String stringSystemActor: systemActors){
            user.getSystemActors().add(SystemActor.valueOf(stringSystemActor));
        }
        userDAO.saveAndFlush(user);
    }


}
