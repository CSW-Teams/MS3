package org.cswteams.ms3.rest;

import org.cswteams.ms3.control.servizi.ControllerServizi;
import org.cswteams.ms3.control.servizi.IControllerServizi;
import org.cswteams.ms3.dto.AssegnazioneTurnoDTO;
import org.cswteams.ms3.dto.ServizioDTO;
import org.cswteams.ms3.dto.UtenteDTO;
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
    IControllerServizi controllerServizi;

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<?> leggiServizi() throws ParseException {
        Set<ServizioDTO> servizi = controllerServizi.leggiServizi();
        return new ResponseEntity<>(servizi, HttpStatus.FOUND);
    }

    @RequestMapping(method = RequestMethod.GET,path = "nome/{nomeServizio}")
    public ResponseEntity<?> leggiServizio(@PathVariable String nomeServizio)  {
        ServizioDTO servizio = controllerServizi.leggiServizioByNome(nomeServizio);
        return new ResponseEntity<>(servizio, HttpStatus.FOUND);
    }

    @RequestMapping(method = RequestMethod.POST, path = "")
    public ResponseEntity<?> creaServizio(@RequestBody(required = true) ServizioDTO servizio) {
        if (servizio != null) {
            return new ResponseEntity<>(controllerServizi.creaServizio(servizio), HttpStatus.ACCEPTED);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
}
