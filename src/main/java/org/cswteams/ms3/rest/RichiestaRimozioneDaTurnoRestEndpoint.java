package org.cswteams.ms3.rest;

import org.cswteams.ms3.control.richiestaRimozioneDaTurno.IControllerRichiestaRimozioneDaTurno;
import org.cswteams.ms3.control.utils.MappaRichiestaRimozioneDaTurno;
import org.cswteams.ms3.dto.RichiestaRimozioneDaTurnoDTO;
import org.cswteams.ms3.entity.RichiestaRimozioneDaTurno;
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
    private IControllerRichiestaRimozioneDaTurno controller;

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<?> creaRichiestaRimozioneDaTurno(@RequestBody RichiestaRimozioneDaTurnoDTO richiestaDTO) {
        RichiestaRimozioneDaTurno r = null;
        if (richiestaDTO == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        try {
            r = controller.creaRichiestaRimozioneDaTurno(richiestaDTO);
        } catch (DatabaseException | AssegnazioneTurnoException e) {
            return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
        }
        return new ResponseEntity<>(MappaRichiestaRimozioneDaTurno.richiestaRimozioneDaTurnoToDTO(r), HttpStatus.ACCEPTED);
    }

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<?> leggiRichiesteRimozioneDaTurno() {
        Set<RichiestaRimozioneDaTurnoDTO> richiesteRimozioneDaTurno = controller.leggiRichiesteRimozioneDaTurno();
        return new ResponseEntity<>(richiesteRimozioneDaTurno, HttpStatus.FOUND);
    }

    @RequestMapping(method = RequestMethod.GET, path = "/pendenti")
    public ResponseEntity<?> leggiRichiesteRimozioneDaTurnoPendenti() {
        Set<RichiestaRimozioneDaTurnoDTO> richiesteRimozioneDaTurnoPendenti = controller.leggiRichiesteRimozioneDaTurnoPendenti();
        return new ResponseEntity<>(richiesteRimozioneDaTurnoPendenti, HttpStatus.FOUND);
    }

    @RequestMapping(method = RequestMethod.GET, path = "/utente/{idUtente}")
    public ResponseEntity<?> leggiRichiesteRimozioneDaTurnoPerUtente(@PathVariable Long idUtente) {
        Set<RichiestaRimozioneDaTurnoDTO> richiesteRimozioneDaTurnoPendenti = controller.leggiRichiesteRimozioneDaTurnoPerUtente(idUtente);
        return new ResponseEntity<>(richiesteRimozioneDaTurnoPendenti, HttpStatus.FOUND);
    }

    @RequestMapping(method = RequestMethod.POST, path = "/risolvi")
    public ResponseEntity<?> risolviRichiestaRimozioneDaTurno(@RequestBody RichiestaRimozioneDaTurnoDTO richiestaRimozioneDaTurnoDTO) {
        RichiestaRimozioneDaTurno r = null;
        if (richiestaRimozioneDaTurnoDTO == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        try {
            r = controller.risolviRichiestaRimozioneDaTurno(richiestaRimozioneDaTurnoDTO);
        } catch (DatabaseException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (AssegnazioneTurnoException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(MappaRichiestaRimozioneDaTurno.richiestaRimozioneDaTurnoToDTO(r), HttpStatus.ACCEPTED);
    }

    @RequestMapping(method = RequestMethod.POST, path = "/{idRichiestaRimozioneDaTurno}/caricaAllegato")
    public ResponseEntity<?> caricaAllegato(@PathVariable Long idRichiestaRimozioneDaTurno, @RequestParam("allegato") MultipartFile allegato) {
        RichiestaRimozioneDaTurno r;
        try {
            r = controller.caricaAllegato(idRichiestaRimozioneDaTurno, allegato);
        } catch (IOException | DatabaseException e) {
            return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
        }
        return new ResponseEntity<>(MappaRichiestaRimozioneDaTurno.richiestaRimozioneDaTurnoToDTO(r), HttpStatus.ACCEPTED);
    }
}
