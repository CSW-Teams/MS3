package org.cswteams.ms3.rest;

import org.cswteams.ms3.control.assegnazioneTurni.IControllerAssegnazioneTurni;
import org.cswteams.ms3.control.scheduler.IControllerScheduler;
import org.cswteams.ms3.control.utils.MappaUtenti;
import org.cswteams.ms3.control.utils.RispostaViolazioneVincoli;
import org.cswteams.ms3.dao.TurnoDao;
import org.cswteams.ms3.dto.AssegnazioneTurnoDTO;
import org.cswteams.ms3.dto.RegistraAssegnazioneTurnoDTO;
import org.cswteams.ms3.entity.AssegnazioneTurno;
import org.cswteams.ms3.entity.Turno;
import org.cswteams.ms3.exception.IllegalAssegnazioneTurnoException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.time.LocalDate;
import java.util.Set;

@RestController
@RequestMapping("/assegnazioneturni/")
public class AssegnazioneTurnoRestEndpoint {

    @Autowired
    private IControllerAssegnazioneTurni controllerAssegnazioneTurni;

    @Autowired
    private IControllerScheduler controllerScheduler;

    @Autowired
    private TurnoDao turnoDao;

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<?> creaTurnoAssegnato(@RequestBody RegistraAssegnazioneTurnoDTO assegnazione) {

        if (assegnazione != null) {
            try {

                //Per convertire il dto in un entità ho bisogno di un turno che dovrebbe essere presente nel databse
                Turno turno = turnoDao.findAllByServizioNomeAndTipologiaTurno(assegnazione.getServizio().getNome(), assegnazione.getTipologiaTurno()).get(0);
                if(turno == null)
                    return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

                //Converto il dto in un entità
                AssegnazioneTurno assegnazioneTurno= new AssegnazioneTurno(
                        LocalDate.of(assegnazione.getAnno(), assegnazione.getMese(),assegnazione.getGiorno()),
                        turno,
                        MappaUtenti.utenteDTOtoEntity(assegnazione.getUtentiReperibili()),
                        MappaUtenti.utenteDTOtoEntity(assegnazione.getUtentiDiGuardia())
                );


                //Se l'utente chiede l'aggiunta forzata di un assegnazione  viene fatto controllo solo sui vincoli non violabili
                controllerScheduler.aggiungiAssegnazioneTurno(assegnazioneTurno,assegnazione.isForced());

                return new ResponseEntity<>(assegnazioneTurno, HttpStatus.ACCEPTED);

            } catch (IllegalAssegnazioneTurnoException e) {

                //Se un vincolo è violato è comunicato all'utente.
                RispostaViolazioneVincoli risposta = new RispostaViolazioneVincoli();
                risposta.setMessage(e.getCause().getMessage());
                return new ResponseEntity<>(risposta,HttpStatus.NOT_ACCEPTABLE);

            }

        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @RequestMapping(method = RequestMethod.GET, path = "/utente_id={idUtente}")
    public ResponseEntity<?> leggiTurniUtente(@PathVariable Long idUtente) throws ParseException {
        if (idUtente != null) {
            Set <AssegnazioneTurnoDTO> c = controllerAssegnazioneTurni.leggiTurniUtente(idUtente);
            if (c == null) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>( c, HttpStatus.FOUND);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<?> leggiTurniAssegnati() throws ParseException {
        Set<AssegnazioneTurnoDTO> tuttiITurni = controllerAssegnazioneTurni.leggiTurniAssegnati();
        return new ResponseEntity<>(tuttiITurni, HttpStatus.FOUND);
    }


}
