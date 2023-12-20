package org.cswteams.ms3.rest;

import org.cswteams.ms3.control.scheduler.IControllerScheduler;
import org.cswteams.ms3.dto.GenerazioneScheduloDTO;
import org.cswteams.ms3.dto.ScheduloDTO;
import org.cswteams.ms3.entity.Schedule;
import org.cswteams.ms3.exception.UnableToBuildScheduleException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/schedule/")
public class ScheduloRestEndpoint {

    @Autowired
    private IControllerScheduler controllerScheduler;

    /*
    * Questo metodo verrà invocato dal frontend per richiedere la generazione di un nuovo schedulo nel range di date
    * passate come parametro
     */
    @RequestMapping(method = RequestMethod.POST, path = "generation")
    public ResponseEntity<?> creaSchedulo(@RequestBody(required = true) GenerazioneScheduloDTO gs) {
        if (gs != null) {

            //Considero solo le richieste di generazione con date ammissibili
            if(gs.getGiornoInizio().isBefore(gs.getGiornoFine())){

                //Chiedo la generazione dello schedulo al controller.
                Schedule schedule = controllerScheduler.createSchedule(gs.getGiornoInizio(),gs.getGiornoFine());
                if(schedule == null)
                    return new ResponseEntity<>( HttpStatus.NOT_ACCEPTABLE);
                if(schedule.isIllegal())
                    return new ResponseEntity<>( HttpStatus.PARTIAL_CONTENT);
                else
                    return new ResponseEntity<>( HttpStatus.ACCEPTED);

            }

        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @RequestMapping(method = RequestMethod.POST, path = "regeneration/id={id}")
    public ResponseEntity<?> ricreaSchedulo(@PathVariable Long id) {
        if (id != null) {

            try {
                if(controllerScheduler.rigeneraSchedule(id))
                    return new ResponseEntity<>(HttpStatus.ACCEPTED);
                else
                    return new ResponseEntity<>(HttpStatus.EXPECTATION_FAILED);

            } catch (UnableToBuildScheduleException e) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }

        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<?> leggiSchedulazioni()  {

        List<ScheduloDTO> set = controllerScheduler.leggiSchedulazioni();
        return new ResponseEntity<>( set, HttpStatus.FOUND);

    }

    @RequestMapping(method = RequestMethod.GET,path = "illegali")
    public ResponseEntity<?> leggiSchedulazioniIllegali()  {

        List<ScheduloDTO> set = controllerScheduler.leggiSchedulazioniIllegali();
        return new ResponseEntity<>( set, HttpStatus.FOUND);

    }

    @RequestMapping(method = RequestMethod.DELETE, path = "id={id}")
    public ResponseEntity<?> deleteSchedulo(@PathVariable Long id)  {

        if (id != null ) {

            //Non è possibile cancellare una vecchia schedulazione. Il controllo è fatto dal controller
            if(controllerScheduler.rimuoviSchedulo(id))
                return new ResponseEntity<>(HttpStatus.OK);
            else
                return new ResponseEntity<>(HttpStatus.EXPECTATION_FAILED);

        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }



}
