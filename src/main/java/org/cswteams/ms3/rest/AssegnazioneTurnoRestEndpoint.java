package org.cswteams.ms3.rest;

import org.cswteams.ms3.control.assegnazioneTurni.IControllerAssegnazioneTurni;
import org.cswteams.ms3.control.scheduler.IControllerScheduler;
import org.cswteams.ms3.dto.AssegnazioneTurnoDTO;
import org.cswteams.ms3.dto.RegistraAssegnazioneTurnoDTO;
import org.cswteams.ms3.entity.AssegnazioneTurno;
import org.cswteams.ms3.exception.AssegnazioneTurnoException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.time.LocalDate;
import java.util.Set;

@RestController
@RequestMapping("/assegnazioneturni/")
public class AssegnazioneTurnoRestEndpoint {

    @Autowired
    private IControllerAssegnazioneTurni controllerAssegnazioneTurni;

    @Autowired
    private IControllerScheduler controllerScheduler;

    @RequestMapping(method = RequestMethod.POST, path = "")
    public ResponseEntity<?> creaTurnoAssegnato(@RequestBody(required = true) RegistraAssegnazioneTurnoDTO c) {

        if (c != null) {
            try {
                AssegnazioneTurno assegnazioneTurno = controllerAssegnazioneTurni.creaTurnoAssegnato(c);
                return new ResponseEntity<>(assegnazioneTurno, HttpStatus.ACCEPTED);
            } catch (AssegnazioneTurnoException e) {
                e.printStackTrace();
                return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
            }
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @RequestMapping(method = RequestMethod.GET, path = "/utente_id={idUtente}")
    public ResponseEntity<?> leggiTurniUtente(@PathVariable Long idUtente) throws ParseException {
        if (idUtente != null) {
            Set <AssegnazioneTurnoDTO> c = controllerAssegnazioneTurni.leggiTurniUtente(idUtente);
            if (c == null) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>( c, HttpStatus.FOUND);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<?> leggiTurniAssegnati() throws ParseException {
        Set<AssegnazioneTurnoDTO> tuttiITurni = controllerAssegnazioneTurni.leggiTurniAssegnati();
        return new ResponseEntity<>(tuttiITurni, HttpStatus.FOUND);
    }


}
