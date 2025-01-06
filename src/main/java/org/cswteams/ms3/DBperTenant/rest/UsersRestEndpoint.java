package org.cswteams.ms3.DBperTenant.rest;

import org.cswteams.ms3.DBperTenant.control.user.IUserController;
import org.cswteams.ms3.DBperTenant.dto.user.UserDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/tenant/users/")
public class UsersRestEndpoint {

    @Autowired
    private IUserController userController;

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<?> getAllUsers() {
        Set<UserDTO> utenti = userController.getAllUsers();
        return new ResponseEntity<>(utenti, HttpStatus.FOUND);
    }

}