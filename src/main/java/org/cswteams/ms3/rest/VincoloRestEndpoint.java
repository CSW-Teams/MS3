package org.cswteams.ms3.rest;

import org.cswteams.ms3.control.vincoli.IControllerVincolo;
import org.cswteams.ms3.entity.constraint.ConfigVincoli;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/vincoli/")
public class VincoloRestEndpoint {

    @Autowired
    IControllerVincolo controllerVincolo;

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<?> leggiVincoli()  {
        return new ResponseEntity<>(controllerVincolo.leggiVincoli(), HttpStatus.FOUND);
    }

    @RequestMapping(method = RequestMethod.GET, path = "configurazione")
    public ResponseEntity<?> leggiConfigurazioneVincoli()  {
        return new ResponseEntity<>(controllerVincolo.leggiConfigurazioneVincoli(), HttpStatus.FOUND);
    }

    @RequestMapping(method = RequestMethod.POST, path = "configurazione")
    public ResponseEntity<?> aggiornaConfigurazioneVincoli(@RequestBody(required = true) ConfigVincoli configurazione) {
        System.out.println("POST");
        if (configurazione != null) {
            return new ResponseEntity<>(controllerVincolo.aggiornaVincoli(configurazione), HttpStatus.ACCEPTED);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }


}
