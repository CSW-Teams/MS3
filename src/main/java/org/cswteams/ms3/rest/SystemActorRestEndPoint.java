package org.cswteams.ms3.rest;

import org.cswteams.ms3.control.actors.ISystemActorController;
import org.cswteams.ms3.dto.systemactor.AllSystemActorsDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/system-actors")
public class SystemActorRestEndPoint {

    @Autowired
    private ISystemActorController systemActorController;

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<?> getAllUsers() {
        AllSystemActorsDTO allSystemActorsDTO;
        try {
            allSystemActorsDTO = systemActorController.getAllSystemActors();
        }catch (Exception e){
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        if(allSystemActorsDTO == null){
            return new ResponseEntity<>(allSystemActorsDTO, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(allSystemActorsDTO, HttpStatus.OK);
    }
}