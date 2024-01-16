package org.cswteams.ms3.rest;


import org.cswteams.ms3.control.login.IControllerLogin;
import org.cswteams.ms3.dto.login.LoggedUserDTO;
import org.cswteams.ms3.dto.login.LoginDTO;
import org.cswteams.ms3.exception.login.InvalidEmailAddressException;
import org.cswteams.ms3.exception.login.InvalidRoleException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/login/")
public class LoginRestEndpoint {

    @Autowired
    private IControllerLogin controllerLogin;


    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<?> login(@RequestBody LoginDTO loginDto) {
        LoggedUserDTO u;
        try {
            u = controllerLogin.authenticateUser(loginDto);
        } catch (InvalidEmailAddressException | InvalidRoleException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        if (u != null) {
            return new ResponseEntity<>(u, HttpStatus.ACCEPTED);
        }

        return new ResponseEntity<>("Username o password errati",HttpStatus.BAD_REQUEST);
    }

}
