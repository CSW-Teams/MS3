package org.cswteams.ms3.rest;

import org.cswteams.ms3.control.shift.IShiftController;
import org.cswteams.ms3.entity.Shift;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;


@RestController
@RequestMapping("/turni/")
public class TurnoRestEndpoint {

    @Autowired
    IShiftController shiftController;

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<?> leggiTurni() throws ParseException {
        //Set<RotationDTO> tuttiITurni = controllerTurni.leggiTurni();
        //return new ResponseEntity<>(tuttiITurni, HttpStatus.FOUND);
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @RequestMapping(method = RequestMethod.GET, path = "/servizio={nomeServizio}")
    public ResponseEntity<?> leggiTurniServizio(@PathVariable String nomeServizio) throws ParseException {
        /*if (nomeServizio != null) {
            Set<RotationDTO> turni = controllerTurni.leggiTurniDiServizio(nomeServizio);
            if (turni == null) return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            return new ResponseEntity<>(turni, HttpStatus.FOUND);
        }*/
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @RequestMapping(method = RequestMethod.POST, path = "")
    public ResponseEntity<?> creaTurno(@RequestBody(required = true) Shift turno) {
        /*
        if (turno != null) {
            try {
                return new ResponseEntity<>(controllerTurni.creaTurno(turno), HttpStatus.ACCEPTED);
            } catch (ShiftException e) {
                return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
            }
        }
        */

        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
}
