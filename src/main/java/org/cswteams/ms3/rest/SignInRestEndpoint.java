package org.cswteams.ms3.rest;

import org.cswteams.ms3.control.registrazione.IControllerRegistrazione;
import org.cswteams.ms3.dto.registration.RegisteredUserDTO;
import org.cswteams.ms3.dto.registration.RegistrationDTO;
import org.cswteams.ms3.exception.registration.RegistrationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/sign-in/")
public class SignInRestEndpoint {

    @Autowired
    private IControllerRegistrazione controllerRegistrazione;

    @PreAuthorize("hasAnyRole('CONFIGURATOR')")
    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<?> registrazione(@RequestBody RegistrationDTO registrationDTO) {

        try {
            RegisteredUserDTO u = controllerRegistrazione.registerUser(registrationDTO);
            if (u != null) {
                return new ResponseEntity<>(u, HttpStatus.ACCEPTED);
            }
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (RegistrationException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}
