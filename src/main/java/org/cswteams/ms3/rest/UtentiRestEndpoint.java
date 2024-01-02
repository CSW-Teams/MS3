package org.cswteams.ms3.rest;

import org.cswteams.ms3.control.utente.IControllerUtente;
import org.cswteams.ms3.dto.UtenteDTO;
import org.cswteams.ms3.entity.Utente;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/utenti/")
public class UtentiRestEndpoint {

    @Autowired
    private IControllerUtente controllerUtente;

    @RequestMapping(method = RequestMethod.POST, path = "")
    public ResponseEntity<?> creaUtente(@RequestBody(required = true) UtenteDTO c) {
        if (c != null) {
            return new ResponseEntity<>(controllerUtente.creaUtente(c), HttpStatus.ACCEPTED);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }


    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<?> leggiUtenti() {
        Set<UtenteDTO> utenti = controllerUtente.leggiUtenti();
        return new ResponseEntity<>(utenti, HttpStatus.FOUND);
    }


    @RequestMapping(method = RequestMethod.GET, path = "/utente_id={idUtente}")
    public ResponseEntity<?> leggiUtente(@PathVariable Long idUtente) {
        UtenteDTO utente = controllerUtente.leggiUtente(idUtente);
        return new ResponseEntity<>(utente, HttpStatus.FOUND);
    }
}
