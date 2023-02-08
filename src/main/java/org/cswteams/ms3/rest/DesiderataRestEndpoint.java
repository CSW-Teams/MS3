package org.cswteams.ms3.rest;

import org.cswteams.ms3.control.desiderata.IControllerDesiderata;
import org.cswteams.ms3.dto.DesiderataDTO;
import org.cswteams.ms3.exception.DatabaseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/desiderate/")
public class DesiderataRestEndpoint {

    @Autowired
    IControllerDesiderata controllerDesiderata;

    @RequestMapping(method = RequestMethod.GET, path = "/utente_id={idUtente}")
    public ResponseEntity<?> leggiDesiderateUtente(@PathVariable Long idUtente){
        if (idUtente != null) {
            List<DesiderataDTO> c = controllerDesiderata.getDesiderateDtoUtente(idUtente);
            if (c == null) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>( c, HttpStatus.FOUND);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @RequestMapping(method = RequestMethod.DELETE, path = "/desiderata_id={idDesiderata}/utente_id={idUtente}")
    public ResponseEntity<?> deleteCategoriaUtente(@PathVariable Long idDesiderata, @PathVariable Long idUtente){
        if (idDesiderata != null && idUtente != null) {
            try {
                controllerDesiderata.cancellaDesiderata(idDesiderata, idUtente);
                return new ResponseEntity<>(HttpStatus.OK);
            } catch (DatabaseException e) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @RequestMapping(method = RequestMethod.POST, path = "/utente_id={idUtente}")
    public ResponseEntity<?> aggiungiDesiderate(@RequestBody(required = true) List<DesiderataDTO> desiderate, @PathVariable Long idUtente) throws Exception {
        if (desiderate != null) {
            return new ResponseEntity<>(controllerDesiderata.aggiungiDesiderate(desiderate, idUtente), HttpStatus.ACCEPTED);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

}
