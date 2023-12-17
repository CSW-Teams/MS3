package org.cswteams.ms3.rest;

import org.cswteams.ms3.control.assegnazioneTurni.IControllerAssegnazioneTurni;
import org.cswteams.ms3.control.scheduler.IControllerScheduler;
import org.cswteams.ms3.control.utils.RispostaViolazioneVincoli;
import org.cswteams.ms3.dto.AssegnazioneTurnoDTO;
import org.cswteams.ms3.dto.ModificaAssegnazioneTurnoDTO;
import org.cswteams.ms3.dto.RegistraAssegnazioneTurnoDTO;
import org.cswteams.ms3.dto.RequestTurnChangeDto;
import org.cswteams.ms3.entity.Schedule;
import org.cswteams.ms3.entity.ViolatedConstraintLogEntry;
import org.cswteams.ms3.exception.AssegnazioneTurnoException;
import org.cswteams.ms3.exception.IllegalScheduleException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.util.Set;

@RestController
@RequestMapping("/assegnazioneturni/")
public class AssegnazioneTurnoRestEndpoint {

    @Autowired
    private IControllerAssegnazioneTurni controllerAssegnazioneTurni;

    @Autowired
    private IControllerScheduler controllerScheduler;


    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<?> creaTurnoAssegnato(@RequestBody RegistraAssegnazioneTurnoDTO assegnazione) {

        Schedule schedule;

        if (assegnazione != null) {
            System.out.println(assegnazione.getMansione());

            // Se l'utente chiede l'aggiunta forzata di un assegnazione viene fatto
            // controllo solo sui vincoli non violabili
            try {
                schedule = controllerScheduler.aggiungiAssegnazioneTurno(assegnazione, assegnazione.isForced());
            } catch (AssegnazioneTurnoException e) {
                return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
            } catch (IllegalScheduleException e) {
                schedule = null;
            }

            if(schedule!=null){
                // Se un vincolo è violato è comunicato all'utente.
                if (schedule.isIllegal()) {
                    RispostaViolazioneVincoli risposta = new RispostaViolazioneVincoli();
                    risposta.getMessagges().add(schedule.getCauseIllegal().getMessage());
                    for (ViolatedConstraintLogEntry vclEntry : schedule.getViolatedConstraintLog()) {
                        risposta.getMessagges().add(vclEntry.getViolation().getMessage());
                    }
                    return new ResponseEntity<>(risposta, HttpStatus.NOT_ACCEPTABLE);
                }

                return new ResponseEntity<>(HttpStatus.ACCEPTED);
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

    /**
     * Permette la modifica di un assegnazione turno già esistente.
     * @param requestTurnChangeDto
     */
    @RequestMapping(method = RequestMethod.PUT, path = "/scambio")
    public ResponseEntity<?> requestTurnChange(@RequestBody RequestTurnChangeDto requestTurnChangeDto)  {

        System.out.println("MI HANNO CHIESTO QUALCOSA");

        System.out.println(requestTurnChangeDto.getConcreteShiftId());
        System.out.println(requestTurnChangeDto.getSenderId());
        System.out.println(requestTurnChangeDto.getReceiverId());

        System.out.println("FINE PARAMETRI");

        //Chiedo al controller di modificare e salvare nel database l'assegnazione turno modificata
        String message;
        try {
            controllerScheduler.requestTurnChange(requestTurnChangeDto);
        } catch (AssegnazioneTurnoException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>("SUCCESS", HttpStatus.ACCEPTED);
    }

    /**
     * Permette la modifica di un assegnazione turno già esistente.
     * @param modificaAssegnazioneTurnoDTO
     * @return
     */
    @RequestMapping(method = RequestMethod.PUT)
    public ResponseEntity<?> modificaAssegnazioneTurno(@RequestBody ModificaAssegnazioneTurnoDTO modificaAssegnazioneTurnoDTO)  {

        //Chiedo al controller di modificare e salvare nel database l'assegnazione turno modificata
        Schedule schedule;
        try {
            schedule = controllerScheduler.modificaAssegnazioneTurno(modificaAssegnazioneTurnoDTO);
        } catch (IllegalScheduleException e) {
            schedule=null;
        }

        // Se la modifica dell'assegnazione turno comporta una violazione dei vincoli, la modifica non va a buon fine
        if (schedule.isIllegal()) {
            RispostaViolazioneVincoli risposta = new RispostaViolazioneVincoli();
            risposta.getMessagges().add(schedule.getCauseIllegal().getMessage());
            for (ViolatedConstraintLogEntry vclEntry : schedule.getViolatedConstraintLog()) {
                risposta.getMessagges().add(vclEntry.getViolation().getMessage());
            }

            return new ResponseEntity<>(risposta, HttpStatus.NOT_ACCEPTABLE);
        }


        return new ResponseEntity<>(schedule, HttpStatus.ACCEPTED);
    }


    @RequestMapping(method = RequestMethod.DELETE, path = "/{idAssegnazione}")
    public ResponseEntity<?> rimuoviAssegnazione(@PathVariable Long idAssegnazione)  {
        if (idAssegnazione != null) {
            if(controllerScheduler.rimuoviAssegnazioneTurno(idAssegnazione)){
                return new ResponseEntity<>(HttpStatus.ACCEPTED);
            }
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }


}
