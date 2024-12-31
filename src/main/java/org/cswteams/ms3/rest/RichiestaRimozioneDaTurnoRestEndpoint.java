package org.cswteams.ms3.rest;

import org.cswteams.ms3.control.requestRemovalFromConcreteShift.IRequestRemovalFromConcreteShiftController;
import org.cswteams.ms3.dto.RequestRemovalFromConcreteShiftDTO;
import org.cswteams.ms3.exception.ConcreteShiftException;
import org.cswteams.ms3.exception.DatabaseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Set;

@RestController
@RequestMapping("/concrete-shifts/retirement-request/")
public class RichiestaRimozioneDaTurnoRestEndpoint {

    @Autowired
    private IRequestRemovalFromConcreteShiftController controller;

    @PreAuthorize("hasAnyRole('DOCTOR')")
    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<?> creaRichiestaRimozioneDaTurno(@RequestBody RequestRemovalFromConcreteShiftDTO requestDTO) {
        if (requestDTO == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        try {
            RequestRemovalFromConcreteShiftDTO ret = controller.createRequest(requestDTO);
            return new ResponseEntity<>(ret, HttpStatus.ACCEPTED);
        } catch (DatabaseException | ConcreteShiftException e) {
            return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
        }
    }

    @PreAuthorize("hasAnyRole('PLANNER')")
    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<?> leggiRichiesteRimozioneDaTurno() {
        Set<RequestRemovalFromConcreteShiftDTO> requests = controller.getAllRequests();
        return new ResponseEntity<>(requests, HttpStatus.FOUND);
    }

    @PreAuthorize("hasAnyRole('DOCTOR', 'PLANNER', 'CONFIGURATOR')")
    @RequestMapping(method = RequestMethod.GET, path = "/pending")
    public ResponseEntity<?> leggiRichiesteRimozioneDaTurnoPendenti() {
        Set<RequestRemovalFromConcreteShiftDTO> richiesteRimozioneDaTurnoPendenti = controller.getPendingRequests();
        return new ResponseEntity<>(richiesteRimozioneDaTurnoPendenti, HttpStatus.FOUND);
    }

    @PreAuthorize("hasAnyRole('DOCTOR')")
    @RequestMapping(method = RequestMethod.GET, path = "/user/{idUtente}")
    public ResponseEntity<?> leggiRichiesteRimozioneDaTurnoPerUtente(@PathVariable Long idUtente) {
        Set<RequestRemovalFromConcreteShiftDTO> richiesteRimozioneDaTurnoPendenti = controller.getRequestsByRequestingDoctorId(idUtente);
        return new ResponseEntity<>(richiesteRimozioneDaTurnoPendenti, HttpStatus.FOUND);
    }

    @PreAuthorize("hasAnyRole('PLANNER')")
    @RequestMapping(method = RequestMethod.POST, path = "/resolve")
    public ResponseEntity<?> risolviRichiestaRimozioneDaTurno(@RequestBody RequestRemovalFromConcreteShiftDTO requestDTO) {
        RequestRemovalFromConcreteShiftDTO ret = null;
        if (requestDTO == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        try {
            ret = controller.reviewRequest(requestDTO);
        } catch (DatabaseException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (ConcreteShiftException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(ret, HttpStatus.ACCEPTED);
    }

    @PreAuthorize("hasAnyRole('DOCTOR')")
    @RequestMapping(method = RequestMethod.POST, path = "/{idRequest}/uploadFile")
    public ResponseEntity<?> uploadFile(@PathVariable Long idRequest, @RequestParam("attachment") MultipartFile attachment) {
        RequestRemovalFromConcreteShiftDTO ret;
        try {
            ret = controller.uploadFile(idRequest, attachment);
        } catch (IOException | DatabaseException e) {
            return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
        }
        return new ResponseEntity<>(ret, HttpStatus.ACCEPTED);
    }
}
