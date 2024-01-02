package org.cswteams.ms3.rest;

import org.cswteams.ms3.control.medicalService.IMedicalServiceController;
import org.cswteams.ms3.dto.medicalservice.MedicalServiceDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/medical-services/")
public class MedicalServicesRestEndpoint {

    @Autowired
    IMedicalServiceController medicalServiceController;

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<?> getAllMedicalServices() {
        Set<MedicalServiceDTO> medicalServices = medicalServiceController.getAllMedicalServices();
        if (medicalServices == null || medicalServices.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(medicalServices, HttpStatus.FOUND);
    }

    @RequestMapping(method = RequestMethod.GET,path = "name/{nomeServizio}")
    public ResponseEntity<?> leggiServizio(@PathVariable String nomeServizio)  {
        MedicalServiceDTO servizio = medicalServiceController.leggiServizioByNome(nomeServizio);
        return new ResponseEntity<>(servizio, HttpStatus.FOUND);
    }

    @RequestMapping(method = RequestMethod.POST, path = "")
    public ResponseEntity<?> creaServizio(@RequestBody(required = true) MedicalServiceDTO servizio) {
        if (servizio != null) {
            return new ResponseEntity<>(medicalServiceController.creaServizio(servizio), HttpStatus.ACCEPTED);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
}
