package org.cswteams.ms3.rest;

import org.cswteams.ms3.control.richiestaRimozioneDaTurno.IControllerRequestRemovalFromConcreteShift;
import org.cswteams.ms3.dto.RequestRemovalFromConcreteShiftDTO;
import org.cswteams.ms3.exception.AssegnazioneTurnoException;
import org.cswteams.ms3.exception.DatabaseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Set;

@RestController
@RequestMapping("/assegnazioneturni/richiesterimozione")
public class RichiestaRimozioneDaTurnoRestEndpoint {

    @Autowired
    private IControllerRequestRemovalFromConcreteShift controller;

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<?> creaRichiestaRimozioneDaTurno(@RequestBody RequestRemovalFromConcreteShiftDTO requestDTO) {
        RequestRemovalFromConcreteShiftDTO ret = null;
        if (requestDTO == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        try {
            ret = controller.createRequest(requestDTO);
        } catch (DatabaseException | AssegnazioneTurnoException e) {
            return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
        }
        return new ResponseEntity<>(ret, HttpStatus.ACCEPTED);
    }

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<?> leggiRichiesteRimozioneDaTurno() {
        Set<RequestRemovalFromConcreteShiftDTO> requests = controller.getAllRequests();
        return new ResponseEntity<>(requests, HttpStatus.FOUND);
    }

    @RequestMapping(method = RequestMethod.GET, path = "/pendenti")
    public ResponseEntity<?> leggiRichiesteRimozioneDaTurnoPendenti() {
        Set<RequestRemovalFromConcreteShiftDTO> richiesteRimozioneDaTurnoPendenti = controller.getPendingRequests();
        return new ResponseEntity<>(richiesteRimozioneDaTurnoPendenti, HttpStatus.FOUND);
    }

    @RequestMapping(method = RequestMethod.GET, path = "/utente/{idUtente}")
    public ResponseEntity<?> leggiRichiesteRimozioneDaTurnoPerUtente(@PathVariable Long idUtente) {
        Set<RequestRemovalFromConcreteShiftDTO> richiesteRimozioneDaTurnoPendenti = controller.getRequestsByRequestingDoctorId(idUtente);
        return new ResponseEntity<>(richiesteRimozioneDaTurnoPendenti, HttpStatus.FOUND);
    }

    @RequestMapping(method = RequestMethod.POST, path = "/risolvi")
    public ResponseEntity<?> risolviRichiestaRimozioneDaTurno(@RequestBody RequestRemovalFromConcreteShiftDTO requestDTO) {
        RequestRemovalFromConcreteShiftDTO ret = null;
        if (requestDTO == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        try {
            ret = controller.reviewRequest(requestDTO);
        } catch (DatabaseException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (AssegnazioneTurnoException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(ret, HttpStatus.ACCEPTED);
    }

    @RequestMapping(method = RequestMethod.POST, path = "/{idRichiestaRimozioneDaTurno}/caricaAllegato")
    public ResponseEntity<?> caricaAllegato(@PathVariable Long request, @RequestParam("attachment") MultipartFile attachment) {
        RequestRemovalFromConcreteShiftDTO ret;
        try {
            ret = controller.uploadFile(request, attachment);
        } catch (IOException | DatabaseException e) {
            return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
        }
        return new ResponseEntity<>(ret, HttpStatus.ACCEPTED);
    }
}
