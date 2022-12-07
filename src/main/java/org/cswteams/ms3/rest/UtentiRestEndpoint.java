package org.cswteams.ms3.rest;

import org.cswteams.ms3.control.utente.IControllerUtente;
import org.cswteams.ms3.entity.Utente;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/utenti/")
public class UtentiRestEndpoint {

    @Autowired
    private IControllerUtente controllerUtente;

    @RequestMapping(method = RequestMethod.POST, path = "")
    public ResponseEntity<?> creaUtente(@RequestBody(required = true) Utente c) {
        if (c != null) {
            return new ResponseEntity<>(controllerUtente.creaUtente(c), HttpStatus.ACCEPTED);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }


    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<?> leggiUtente() {
        List<Utente> tuttiITurni = controllerUtente.leggiUtenti();
        return new ResponseEntity<>(tuttiITurni, HttpStatus.FOUND);
    }
}
