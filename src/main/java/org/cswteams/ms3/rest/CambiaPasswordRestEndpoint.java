package org.cswteams.ms3.rest;


import org.cswteams.ms3.control.passwordChange.IPasswordChange;
import org.cswteams.ms3.dto.changePassword.ChangePasswordDTO;
import org.cswteams.ms3.exception.DatabaseException;
import org.cswteams.ms3.exception.changePassword.WrongOldPasswordException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/password/")
public class CambiaPasswordRestEndpoint {

    @Autowired
    private IPasswordChange controllerPassword;


    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<?> changePassword(@RequestBody ChangePasswordDTO dto) {
        try {
            ChangePasswordDTO returnDto = controllerPassword.changePassword(dto);
            return new ResponseEntity<>(returnDto, HttpStatus.ACCEPTED);
        } catch (DatabaseException | WrongOldPasswordException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

}
