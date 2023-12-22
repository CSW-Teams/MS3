package org.cswteams.ms3.rest;

import org.cswteams.ms3.control.medicalService.IMedicalServiceController;
import org.cswteams.ms3.dto.MedicalServiceDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.util.Set;

@RestController
@RequestMapping("/servizi/")
public class ServizioRestEndpoint {

    @Autowired
    IMedicalServiceController controllerServizi;

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<?> leggiServizi() throws ParseException {
        Set<MedicalServiceDTO> servizi = controllerServizi.leggiServizi();
        if (servizi == null || servizi.isEmpty()) {
            System.out.println("sono qui");
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        System.out.println("invece sono qui");
        return new ResponseEntity<>(servizi, HttpStatus.FOUND);
    }

    @RequestMapping(method = RequestMethod.GET,path = "nome/{nomeServizio}")
    public ResponseEntity<?> leggiServizio(@PathVariable String nomeServizio)  {
        MedicalServiceDTO servizio = controllerServizi.leggiServizioByNome(nomeServizio);
        return new ResponseEntity<>(servizio, HttpStatus.FOUND);
    }

    @RequestMapping(method = RequestMethod.POST, path = "")
    public ResponseEntity<?> creaServizio(@RequestBody(required = true) MedicalServiceDTO servizio) {
        if (servizio != null) {
            return new ResponseEntity<>(controllerServizi.creaServizio(servizio), HttpStatus.ACCEPTED);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
}
