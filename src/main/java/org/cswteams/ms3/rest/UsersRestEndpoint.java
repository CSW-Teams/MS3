package org.cswteams.ms3.rest;

import org.cswteams.ms3.control.user.IUserController;
import org.cswteams.ms3.dto.DoctorDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/users/")
public class UsersRestEndpoint {

    @Autowired
    private IUserController controllerUtente;

    @RequestMapping(method = RequestMethod.POST, path = "")
    public ResponseEntity<?> createUser(@RequestBody(required = true) DoctorDTO doctor) {
        if (doctor != null) {
            return new ResponseEntity<>(controllerUtente.createUser(doctor), HttpStatus.ACCEPTED);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }


    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<?> getAllUsers() {
        Set<DoctorDTO> utenti = controllerUtente.getAllUsers();
        System.out.println("fadsfjadsfdw " + utenti);
        return new ResponseEntity<>(utenti, HttpStatus.FOUND);
    }


    @RequestMapping(method = RequestMethod.GET, path = "/user_id={idUtente}")
    public ResponseEntity<?> getSingleUser(@PathVariable Long idUtente) {
        DoctorDTO utente = controllerUtente.getSingleUser(idUtente);
        return new ResponseEntity<>(utente, HttpStatus.FOUND);
    }
}
