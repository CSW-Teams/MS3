package org.cswteams.ms3.rest;

import org.cswteams.ms3.control.turni.IControllerTurni;
import org.cswteams.ms3.entity.Turno;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.List;

@RestController
@RequestMapping("/turni/")
public class TurnoRestEndpoint {

    @Autowired
    private IControllerTurni controllerTurni;

    @RequestMapping(method = RequestMethod.POST, path = "")
    public ResponseEntity<?> creaTurno(@RequestBody(required = true) Turno c) {
        if (c != null) {
            return new ResponseEntity<>(controllerTurni.creaTurno(c), HttpStatus.ACCEPTED);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @RequestMapping(method = RequestMethod.GET, path = "/utente_id={idUtente}")
    public ResponseEntity<?> leggiTurniUtente(@PathVariable Long idUtente) {
        if (idUtente != null) {
            List <Turno> c = controllerTurni.leggiTurniUtente(idUtente);
            if (c == null) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>( c, HttpStatus.FOUND);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }



    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<?> leggiTurni() {
        List<Turno> tuttiITurni = controllerTurni.leggiTurni();
        return new ResponseEntity<>(tuttiITurni, HttpStatus.FOUND);
    }


    /** TO FIX

    @RequestMapping(method = RequestMethod.GET, path = "search")
    public ResponseEntity<?> cercaTurnoPerSlot(@RequestParam(name = "inizio", required = false) String inizio, @RequestParam(name = "fine", required = false) String fine) {

        List <Turno> turni = controllerTurni.leggiTurnoDaSlot( Timestamp.valueOf(inizio), Timestamp.valueOf(fine));
        return new ResponseEntity<>(turni, HttpStatus.FOUND);
    }
    */

}