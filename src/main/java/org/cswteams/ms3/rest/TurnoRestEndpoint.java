package org.cswteams.ms3.rest;

import org.cswteams.ms3.control.turni.IControllerTurni;
import org.cswteams.ms3.dto.ServizioDTO;
import org.cswteams.ms3.dto.TurnoDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.util.Set;

@RestController
@RequestMapping("/turni/")
public class TurnoRestEndpoint {

    @Autowired
    IControllerTurni controllerTurni;

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<?> leggiTurni() throws ParseException {
        Set<TurnoDTO> tuttiITurni = controllerTurni.leggiTurni();
        return new ResponseEntity<>(tuttiITurni, HttpStatus.FOUND);
    }

    @RequestMapping(method = RequestMethod.GET, path = "/servizio={nomeServizio}")
    public ResponseEntity<?> leggiTurniServizio(@PathVariable String nomeServizio) throws ParseException {
        if (nomeServizio != null) {
            Set<TurnoDTO> turni = controllerTurni.leggiTurniDiServizio(nomeServizio);
            if (turni == null) return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            return new ResponseEntity<>(turni, HttpStatus.FOUND);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @RequestMapping(method = RequestMethod.POST, path = "")
    public ResponseEntity<?> creaTurno(@RequestBody(required = true) TurnoDTO turno) {
        if (turno != null) {
            return new ResponseEntity<>(controllerTurni.creaTurno(turno), HttpStatus.ACCEPTED);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
}
