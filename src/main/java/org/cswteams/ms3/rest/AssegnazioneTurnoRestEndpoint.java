package org.cswteams.ms3.rest;

import org.cswteams.ms3.control.assegnazioneTurni.IControllerAssegnazioneTurni;
import org.cswteams.ms3.control.scheduler.IControllerScheduler;
import org.cswteams.ms3.control.utils.MappaUtenti;
import org.cswteams.ms3.control.utils.RispostaViolazioneVincoli;
import org.cswteams.ms3.dao.TurnoDao;
import org.cswteams.ms3.dto.AssegnazioneTurnoDTO;
import org.cswteams.ms3.dto.ModificaAssegnazioneTurnoDTO;
import org.cswteams.ms3.dto.RegistraAssegnazioneTurnoDTO;
import org.cswteams.ms3.entity.AssegnazioneTurno;
import org.cswteams.ms3.entity.Schedule;
import org.cswteams.ms3.entity.Turno;
import org.cswteams.ms3.entity.ViolatedConstraintLogEntry;
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

        Schedule schedule;

        if (assegnazione != null) {

            // Per convertire il dto in un entità ho bisogno di un turno che dovrebbe essere
            // presente nel databse
            Turno turno = turnoDao.findAllByServizioNomeAndTipologiaTurno(assegnazione.getServizio().getNome(),
                    assegnazione.getTipologiaTurno()).get(0);
            if (turno == null)
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

            // Converto il dto in un entità
            AssegnazioneTurno assegnazioneTurno = new AssegnazioneTurno(
                    LocalDate.of(assegnazione.getAnno(), assegnazione.getMese(), assegnazione.getGiorno()),
                    turno,
                    MappaUtenti.utenteDTOtoEntity(assegnazione.getUtentiReperibili()),
                    MappaUtenti.utenteDTOtoEntity(assegnazione.getUtentiDiGuardia()));

            // Se l'utente chiede l'aggiunta forzata di un assegnazione viene fatto
            // controllo solo sui vincoli non violabili
            schedule = controllerScheduler.aggiungiAssegnazioneTurno(assegnazioneTurno, assegnazione.isForced());

            // Se un vincolo è violato è comunicato all'utente.
            if (schedule.isIllegal()) {
                RispostaViolazioneVincoli risposta = new RispostaViolazioneVincoli();
                risposta.getMessagges().add(schedule.getCauseIllegal().getMessage());
                for (ViolatedConstraintLogEntry vclEntry : schedule.getViolatedConstraintLog()) {
                    risposta.getMessagges().add(vclEntry.getViolation().getMessage());
                }
                return new ResponseEntity<>(risposta, HttpStatus.NOT_ACCEPTABLE);
            }

            return new ResponseEntity<>(assegnazioneTurno, HttpStatus.ACCEPTED);

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
     * @param modificaAssegnazioneTurnoDTO
     * @return
     */
    @RequestMapping(method = RequestMethod.PUT)
    public ResponseEntity<?> modificaAssegnazioneTurno(@RequestBody ModificaAssegnazioneTurnoDTO modificaAssegnazioneTurnoDTO)  {

        //Chiedo al controller di modificare e salvare nel database l'assegnazione turno modificata
        Schedule schedule= controllerScheduler.modificaAssegnazioneTurno(modificaAssegnazioneTurnoDTO);


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


}
