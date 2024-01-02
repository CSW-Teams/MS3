package org.cswteams.ms3.rest;


import org.cswteams.ms3.control.cambiaPassword.IControllerPassword;
import org.cswteams.ms3.dto.PasswordDTO;
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
    private IControllerPassword controllerpwd;


    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<?> cambiaPass(@RequestBody PasswordDTO dto) {
        try {
            controllerpwd.cambiaPassword(dto);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }

}
