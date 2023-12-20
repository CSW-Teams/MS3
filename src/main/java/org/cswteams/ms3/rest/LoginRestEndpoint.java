package org.cswteams.ms3.rest;


import org.cswteams.ms3.control.login.IControllerLogin;
import org.cswteams.ms3.dto.LoginDTO;
import org.cswteams.ms3.dto.DoctorDTO;
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
        System.out.println(loginDto.getEmail() + " " + loginDto.getPassword());
        DoctorDTO u = controllerLogin.authenticateUser(loginDto);
        System.out.println(u.getName());
        if (u != null) {
            return new ResponseEntity<>(u, HttpStatus.ACCEPTED);
        }

        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

}
