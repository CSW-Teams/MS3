package org.cswteams.ms3.rest;

import org.cswteams.ms3.control.concreteShift.IConcreteShiftController;
import org.cswteams.ms3.control.scambioTurno.IControllerScambioTurno;
import org.cswteams.ms3.control.scheduler.IControllerScheduler;
import org.cswteams.ms3.control.utils.RispostaViolazioneVincoli;
import org.cswteams.ms3.dto.*;
import org.cswteams.ms3.entity.Schedule;
import org.cswteams.ms3.exception.AssegnazioneTurnoException;
import org.cswteams.ms3.exception.IllegalScheduleException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/assegnazioneturni/")
public class AssegnazioneTurnoRestEndpoint {

    @Autowired
    private IConcreteShiftController controllerAssegnazioneTurni;

    @Autowired
    private IControllerScheduler controllerScheduler;

    @Autowired
    private IControllerScambioTurno controllerScambioTurno;


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
                /*
                if (schedule.isIllegal()) {
                    RispostaViolazioneVincoli risposta = new RispostaViolazioneVincoli();
                    risposta.getMessagges().add(schedule.getCauseIllegal().getMessage());
                    for (ViolatedConstraintLogEntry vclEntry : schedule.getViolatedConstraintLog()) {
                        risposta.getMessagges().add(vclEntry.getViolation().getMessage());
                    }
                    return new ResponseEntity<>(risposta, HttpStatus.NOT_ACCEPTABLE);
                }*/

                return new ResponseEntity<>(HttpStatus.ACCEPTED);
            }

        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @RequestMapping(method = RequestMethod.GET, path = "/utente_id={idUtente}")
    public ResponseEntity<?> leggiTurniUtente(@PathVariable Long idUtente) throws ParseException {
        if (idUtente != null) {
            Set <ConcreteShiftDTO> c = controllerAssegnazioneTurni.leggiTurniUtente(idUtente);
            if (c == null) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>( c, HttpStatus.FOUND);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<?> leggiTurniAssegnati() throws ParseException {
        Set<ConcreteShiftDTO> tuttiITurni = controllerAssegnazioneTurni.leggiTurniAssegnati();
        return new ResponseEntity<>(tuttiITurni, HttpStatus.FOUND);
    }

    /**
     * Permette la modifica di un assegnazione turno già esistente.
     * @param requestTurnChangeDto
     */
    @RequestMapping(method = RequestMethod.PUT, path = "/scambio")
    public ResponseEntity<?> requestShiftChange(@RequestBody RequestTurnChangeDto requestTurnChangeDto)  {

        try {
            controllerScambioTurno.requestTurnChange(requestTurnChangeDto);
        } catch (AssegnazioneTurnoException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>("SUCCESS", HttpStatus.ACCEPTED);
    }

    /**
     * Ritorna le richieste iniziate dall'id indicato
     * @param idUtente
     */
    @RequestMapping(method = RequestMethod.GET, path = "/scambio/by/utente_id={idUtente}")
    public ResponseEntity<?> getRequestsBySender(@PathVariable Long idUtente)  {

        if (idUtente != null) {
            List<ViewUserTurnRequestsDTO> requests = controllerScambioTurno.getRequestsBySender(idUtente);
            if (requests == null) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>( requests, HttpStatus.FOUND);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @RequestMapping(method = RequestMethod.GET, path = "/scambio/to/utente_id={idUtente}")
    public ResponseEntity<?> getRequestsToSender(@PathVariable Long idUtente)  {

        if (idUtente != null) {
            List<ViewUserTurnRequestsDTO> requests = controllerScambioTurno.getRequestsToSender(idUtente);
            if (requests == null) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>( requests, HttpStatus.FOUND);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
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
        assert schedule != null;
        if (!schedule.getViolatedConstraints().isEmpty()) {

            RispostaViolazioneVincoli risposta = new RispostaViolazioneVincoli();
            /*
            risposta.getMessagges().add(schedule.getViolatedConstraints());
            for (ViolatedConstraintLogEntry vclEntry : schedule.getViolatedConstraintLog()) {
                risposta.getMessagges().add(vclEntry.getViolation().getMessage());
            }*/

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
