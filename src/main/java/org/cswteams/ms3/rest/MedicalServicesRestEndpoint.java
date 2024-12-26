package org.cswteams.ms3.rest;

import org.cswteams.ms3.control.medicalService.IMedicalServiceController;
import org.cswteams.ms3.dto.medicalservice.AvailableTasksTypesDTO;
import org.cswteams.ms3.dto.medicalservice.MedicalServiceDTO;
import org.cswteams.ms3.dto.medicalservice.MedicalServiceCreationDTO;
import org.cswteams.ms3.dto.medicalservice.MedicalServiceWithTaskAssignmentsDTO;
import org.cswteams.ms3.exception.DatabaseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/medical-services/")
@PreAuthorize("hasAnyRole('CONFIGURATOR', 'DOCTOR', 'PLANNER')")
public class MedicalServicesRestEndpoint {

    @Autowired
    IMedicalServiceController medicalServiceController;

    @PreAuthorize("hasAnyAuthority('configurator:get', 'doctor:get', 'planner:get')")
    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<?> getAllMedicalServices() {
        Set<MedicalServiceWithTaskAssignmentsDTO> medicalServices = medicalServiceController.getAllMedicalServices();
        if (medicalServices == null || medicalServices.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(medicalServices, HttpStatus.FOUND);
    }

    @PreAuthorize("hasAnyAuthority('configurator:get', 'doctor:get', 'planner:get')")
    @RequestMapping(method = RequestMethod.GET, path = "name/{serviceName}")
    public ResponseEntity<?> leggiServizio(@PathVariable String serviceName) {
        MedicalServiceDTO service = medicalServiceController.getServiceByName(serviceName);
        return new ResponseEntity<>(service, HttpStatus.FOUND);
    }

    @PreAuthorize("hasAnyAuthority('configurator:get', 'doctor:get', 'planner:get')")
    @RequestMapping(method = RequestMethod.GET, path = "available-task-types")
    public ResponseEntity<?> getAvailableTaskTypes() {
        AvailableTasksTypesDTO taskTypes = medicalServiceController.getAvailableTaskTypes();
        return new ResponseEntity<>(taskTypes, HttpStatus.FOUND);
    }

    @PreAuthorize("hasAnyAuthority('configurator:post', 'planner:post')")
    @RequestMapping(method = RequestMethod.POST, path = "")
    public ResponseEntity<?> creaServizio(@RequestBody(required = true) MedicalServiceCreationDTO service) {
        if (service != null) {
            return new ResponseEntity<>(medicalServiceController.createService(service), HttpStatus.ACCEPTED);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @PreAuthorize("hasAnyAuthority('configurator:post', 'planner:post')")
    @RequestMapping(method = RequestMethod.POST, path = "update")
    public ResponseEntity<?> updateService(@RequestBody(required = true) MedicalServiceDTO service) {
        if (service != null) {
            try {
                return new ResponseEntity<>(medicalServiceController.updateService(service), HttpStatus.ACCEPTED);
            } catch (DatabaseException e) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            } catch (RuntimeException e) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @PreAuthorize("hasAnyAuthority('configurator:get', 'planner:get')")
    @RequestMapping(method = RequestMethod.POST, path = "delete")
    public ResponseEntity<?> deleteService(@RequestBody(required = true) Long serviceId) {
        if (serviceId != null) {
            try {
                return new ResponseEntity<>(medicalServiceController.deleteService(serviceId), HttpStatus.ACCEPTED);
            } catch (DatabaseException e) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
}
