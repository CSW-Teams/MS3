package org.cswteams.ms3.rest;

import org.cswteams.ms3.control.doctor.IDoctorController;
import org.cswteams.ms3.control.user.IUserController;
import org.cswteams.ms3.dto.specialization.DoctorSpecializationDTO;
import org.cswteams.ms3.dto.systemactor.UserSystemActorDTO;
import org.cswteams.ms3.dto.user.UserCreationDTO;
import org.cswteams.ms3.dto.user.UserDTO;
import org.cswteams.ms3.dto.user.UserDetailsDTO;
import org.cswteams.ms3.dto.userprofile.SingleUserProfileDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/users/")
public class UsersRestEndpoint {

    @Autowired
    private IUserController userController;

    @RequestMapping(method = RequestMethod.POST, path = "")
    public ResponseEntity<?> createUser(@RequestBody() UserCreationDTO doctor) {
        if (doctor != null) {
            userController.createUser(doctor);
            return new ResponseEntity<>(HttpStatus.ACCEPTED);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }


    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<?> getAllUsers() {
        Set<UserDTO> utenti = userController.getAllUsers();
        return new ResponseEntity<>(utenti, HttpStatus.FOUND);
    }


    @RequestMapping(method = RequestMethod.GET, path = "/user_id={userId}")
    public ResponseEntity<?> getSingleUser(@PathVariable Long userId) {
        UserDetailsDTO u = userController.getSingleUser(userId);
        return new ResponseEntity<>(u, HttpStatus.FOUND);
    }


    @RequestMapping(method = RequestMethod.GET, path = "/user-profile/user_id={userId}")
    public ResponseEntity<?> getSingleUserProfileInfos(@PathVariable Long userId) {
        if(userId < 0){
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }else{
            SingleUserProfileDTO singleUserProfileDTO = userController.getSingleUserProfileInfos(userId);
            if(singleUserProfileDTO == null){
                return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
            }else if(singleUserProfileDTO.getId() == null || singleUserProfileDTO.getId() == -1){
                return new ResponseEntity<>(singleUserProfileDTO, HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(singleUserProfileDTO, HttpStatus.OK);
        }
    }


    @RequestMapping(method = RequestMethod.DELETE, path = "/user-profile/delete-system-actor")
    public ResponseEntity<?> deleteUserSystemActor(@RequestBody() UserSystemActorDTO userSystemActorDTO) {
        if(userSystemActorDTO.getUserID() < 0){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }else{
            try{
                userController.deleteUserSystemActor(userSystemActorDTO.getUserID(), userSystemActorDTO.getSystemActor());
            }catch (Exception e){
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.POST, path = "/user-profile/add-system-actor")
    public ResponseEntity<?> addUserSystemActor(@RequestBody() DoctorSpecializationDTO doctorSpecializationDTO) {
        /*if(doctorSpecializationDTO.getDoctorID() < 0){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }else{
            try{
                doctorController.addDoctorSpecialization(doctorSpecializationDTO.getDoctorID(), doctorSpecializationDTO.getSpecialization());
            }catch (Exception e){
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }*/
        //TODO
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
