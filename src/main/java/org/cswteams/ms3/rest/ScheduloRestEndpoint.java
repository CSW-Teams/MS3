package org.cswteams.ms3.rest;

import org.cswteams.ms3.control.scheduler.IControllerScheduler;
import org.cswteams.ms3.control.scheduler.UnableToBuildScheduleException;
import org.cswteams.ms3.control.utente.IControllerUtente;
import org.cswteams.ms3.dto.GenerazioneScheduloDTO;
import org.cswteams.ms3.dto.UtenteDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

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
            if(gs.getStartDate().isBefore(gs.getEndDate())){

                try {
                    //Chiedo la generazione dello schedulo al controller.
                    return new ResponseEntity<>(controllerScheduler.createSchedule(gs.getStartDate(),gs.getEndDate()), HttpStatus.ACCEPTED);

                } catch (UnableToBuildScheduleException e) {

                    //Non è possibile generare uno schedulo
                    return new ResponseEntity<>(HttpStatus.EXPECTATION_FAILED);
                }
            }

        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

}
