package org.cswteams.ms3.rest;

import org.cswteams.ms3.control.vincoli.IControllerVincolo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/vincoli/")
public class VincoloRestEndpoint {

    @Autowired
    IControllerVincolo controllerVincolo;

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<?> leggiVincoli()  {
        return new ResponseEntity<>(controllerVincolo.leggiVincoli(), HttpStatus.FOUND);
    }

}
