package org.cswteams.ms3.rest;

import org.cswteams.ms3.control.registrazione.IControllerRegistrazione;
import org.cswteams.ms3.dto.DoctorDTO;
import org.cswteams.ms3.dto.RegistrazioneDTO;
import org.cswteams.ms3.dto.UtenteDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/registrazione/")
public class RegistrazioneRestEndpoint {

    @Autowired
    private IControllerRegistrazione controllerRegistrazione;

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<?> registrazione(@RequestBody RegistrazioneDTO registrazioneDTO) {
        DoctorDTO u = controllerRegistrazione.registraUtente(registrazioneDTO);
        if (u != null) {
            return new ResponseEntity<>(u, HttpStatus.ACCEPTED);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
}
