package org.cswteams.ms3.rest;

import org.cswteams.ms3.control.concreteShift.IConcreteShiftController;
import org.cswteams.ms3.control.scheduler.ISchedulerController;
import org.cswteams.ms3.control.utils.RispostaViolazioneVincoli;
import org.cswteams.ms3.dto.concreteshift.ConcreteShiftDTO;
import org.cswteams.ms3.dto.ModifyConcreteShiftDTO;
import org.cswteams.ms3.dto.RegisterConcreteShiftDTO;
import org.cswteams.ms3.entity.Schedule;
import org.cswteams.ms3.entity.constraint.Constraint;
import org.cswteams.ms3.exception.AssegnazioneTurnoException;
import org.cswteams.ms3.exception.IllegalScheduleException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.util.Set;

@RestController
@RequestMapping("/concrete-shifts/")
public class ConcreteShiftRestEndpoint {

    @Autowired
    private IConcreteShiftController controllerAssegnazioneTurni;

    @Autowired
    private ISchedulerController controllerScheduler;

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<?> creaTurnoAssegnato(@RequestBody RegisterConcreteShiftDTO assegnazione) {

        Schedule schedule;

        if (assegnazione != null) {
            System.out.println(assegnazione.getMansione());

            // Se l'utente chiede l'aggiunta forzata di un assegnazione viene fatto
            // controllo solo sui vincoli non violabili
            try {
                schedule = controllerScheduler.addConcreteShift(assegnazione, assegnazione.isForced());
            } catch (AssegnazioneTurnoException e) {
                return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
            } catch (IllegalScheduleException e) {
                schedule = null;
            }

            if(schedule!=null){
                // Se un vincolo è violato è comunicato all'utente.

                if (schedule.getCauseIllegal()!=null) {
                    RispostaViolazioneVincoli risposta = new RispostaViolazioneVincoli();
                    risposta.getMessagges().add(schedule.getCauseIllegal().getMessage());
                    for (Constraint vclEntry : schedule.getViolatedConstraints()) {
                        risposta.getMessagges().add(vclEntry.getDescription());
                    }
                    return new ResponseEntity<>(risposta, HttpStatus.NOT_ACCEPTABLE);
                }

                return new ResponseEntity<>(HttpStatus.ACCEPTED);
            }

        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @RequestMapping(method = RequestMethod.GET, path = " /user_id={userID}")
    public ResponseEntity<?> leggiTurniUtente(@PathVariable Long userID) throws ParseException {
        if (userID != null) {
            Set <ConcreteShiftDTO> c = controllerAssegnazioneTurni.leggiTurniUtente(userID);
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
     * @param modifyConcreteShiftDTO
     * @return
     */
    @RequestMapping(method = RequestMethod.PUT)
    public ResponseEntity<?> modificaAssegnazioneTurno(@RequestBody ModifyConcreteShiftDTO modifyConcreteShiftDTO)  {

        //Chiedo al controller di modificare e salvare nel database l'assegnazione turno modificata
        Schedule schedule;
        try {
            schedule = controllerScheduler.modifyConcreteShift(modifyConcreteShiftDTO);
        } catch (IllegalScheduleException e) {
            schedule=null;
        }

        // Se la modifica dell'assegnazione turno comporta una violazione dei vincoli, la modifica non va a buon fine
        assert schedule != null;
        if (!schedule.getViolatedConstraints().isEmpty()) {

            RispostaViolazioneVincoli risposta = new RispostaViolazioneVincoli();

            risposta.getMessagges().add(schedule.getViolatedConstraints().get(schedule.getViolatedConstraints().size() - 1).getDescription());
            for (Constraint vclEntry : schedule.getViolatedConstraints()) {
                risposta.getMessagges().add(vclEntry.getDescription());
            }

            return new ResponseEntity<>(risposta, HttpStatus.NOT_ACCEPTABLE);
        }


        return new ResponseEntity<>(schedule, HttpStatus.ACCEPTED);
    }


    @RequestMapping(method = RequestMethod.DELETE, path = "/{idAssegnazione}")
    public ResponseEntity<?> rimuoviAssegnazione(@PathVariable Long idAssegnazione)  {
        if (idAssegnazione != null) {
            if(controllerScheduler.removeConcreteShift(idAssegnazione)){
                return new ResponseEntity<>(HttpStatus.ACCEPTED);
            }
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }


}
