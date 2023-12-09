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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

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

    @RequestMapping(method = RequestMethod.PUT)
    public ResponseEntity<?> risolviRichiestaRimozioneDaTurno(@RequestBody RichiestaRimozioneDaTurnoDTO richiestaRimozioneDaTurnoDTO) {
        RichiestaRimozioneDaTurno r = null;
        if (richiestaRimozioneDaTurnoDTO == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        try {
            r = controller.risolviRichiestaRimozioneDaTurno(richiestaRimozioneDaTurnoDTO.getId(), richiestaRimozioneDaTurnoDTO.isEsito());
        } catch (DatabaseException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(MappaRichiestaRimozioneDaTurno.richiestaRimozioneDaTurnoToDTO(r), HttpStatus.ACCEPTED);
    }
}
