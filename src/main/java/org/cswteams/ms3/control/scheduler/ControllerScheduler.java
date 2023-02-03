package org.cswteams.ms3.control.scheduler;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;
import java.util.Optional;

import org.cswteams.ms3.control.assegnazioneTurni.IControllerAssegnazioneTurni;
import org.cswteams.ms3.control.utils.MappaUtenti;
import org.cswteams.ms3.dao.*;
import org.cswteams.ms3.dto.ModificaAssegnazioneTurnoDTO;
import org.cswteams.ms3.dto.RegistraAssegnazioneTurnoDTO;
import org.cswteams.ms3.entity.AssegnazioneTurno;
import org.cswteams.ms3.entity.Schedule;
import org.cswteams.ms3.entity.Turno;
import org.cswteams.ms3.entity.Utente;
import org.cswteams.ms3.exception.AssegnazioneTurnoException;
import org.cswteams.ms3.exception.UnableToBuildScheduleException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;


@Service
public class ControllerScheduler implements IControllerScheduler{

    @Autowired
    private UtenteDao utenteDao;

    @Autowired
    private TurnoDao turnoDao;

    @Autowired
    private ScheduleDao scheduleDao;

    @Autowired
    private VincoloDao vincoloDao;

    @Autowired
    private AssegnazioneTurnoDao assegnazioneTurnoDao;



    private ScheduleBuilder scheduleBuilder;


    /**
     * Permette la creazione di un nuovo schedulo specificando data inizio e data fine della generazione.
     * @param startDate giorno di inizio della validità della pianificazione
     * @param endDate giorno di fine (compreso) della validità della pianificazione
     * @return
     * @throws UnableToBuildScheduleException
     */
    @Override
    public Schedule createSchedule(LocalDate startDate, LocalDate endDate) throws UnableToBuildScheduleException {

        //Data che uso per scorre l'intervallo di giorni
        LocalDate currentDay = startDate;
        List<AssegnazioneTurno> allAssegnazioni = new ArrayList<>();

        // creo assegnazioni associando una data a ogni turno.
        //Scorro tutti i giorni dell'intervallo. Per ogni giorno scorro tutti i turni.
        while(!currentDay.isAfter(endDate)){
            for(Turno turno : turnoDao.findAll()){
                
                // Possiamo assegnare questo turno a questo giorno solo se il giorno
                // della settimana è previsto tra quelli ammissibili del turno
                if (turno.getGiorniDiValidità().isDayOfWeekIncluded(currentDay.getDayOfWeek())){
                    allAssegnazioni.add(new AssegnazioneTurno(currentDay,turno));
                }
                
            }

            //Mi sposto al giorno seguente.
            currentDay = currentDay.plusDays(1);
        }

        //Creo uno schedule builder ad ogni nuova pianificazione
        this.scheduleBuilder = new ScheduleBuilder(
            startDate,  // data inizio pianificazione
            endDate,    // data fine pianificazione
            vincoloDao.findAll(),    // tutti i vincoli da rispettare quando si assegna una persona a un turno
            allAssegnazioni,    // assegnazioni di turno con data (senza partecipanti)
            utenteDao.findAll() // tutti i candidati da allocare ai turni
            );

        return  scheduleDao.save(this.scheduleBuilder.build());
        
    }

    /**
     * Permette di aggiungere una nuova assegnazione ad uno schedulo gia esistente. Il controller andrà a cercare
     * lo schedulo contenente il giorno della nuova assegnazione e lo passerà al builder.
     * Se forced è true l'assegnazione verrà aggiunta solo se vengono rispetatti i vincoli non violabili.
     * Se forced è false l'assegnazione verà aggiunta se vengono rispettati tutti i vincoli.
     */
    public Schedule aggiungiAssegnazioneTurno(AssegnazioneTurno assegnazioneTurno,boolean forced) {

        Schedule schedule;
        
        //creo un nuovo builder passandogli uno schedulo già esistente
        this.scheduleBuilder = new ScheduleBuilder(
                vincoloDao.findAll(), // tutti i vincoli da rispettare quando si assegna una persona a un turno
                utenteDao.findAll(),  // tutti i candidati da allocare ai turni
                scheduleDao.findByDateBetween(assegnazioneTurno.getDataEpochDay()) //Schedulo gia esistente
        );

        schedule = this.scheduleBuilder.addAssegnazioneTurno(assegnazioneTurno,forced);
        
        // we commit changes to schedule only if they do not taint it
        if (!schedule.isIllegal()){
            scheduleDao.save(schedule);
        }
        
        return schedule;
    }

    public void rimuoviAssegnazioneTurno(AssegnazioneTurno assegnazioneTurnoOld) {
        Schedule schedule = scheduleDao.findByDateBetween(assegnazioneTurnoOld.getDataEpochDay());
        schedule.getAssegnazioniTurno().remove(assegnazioneTurnoOld);

        scheduleDao.flush();
        assegnazioneTurnoDao.delete(assegnazioneTurnoOld);
    }

    @Override
    public boolean rimuoviAssegnazioneTurno(Long idAssegnazione) {
        Optional<AssegnazioneTurno> assegnazioneTurno = assegnazioneTurnoDao.findById(idAssegnazione);
        if(assegnazioneTurno.isEmpty())
            return false;

        this.rimuoviAssegnazioneTurno(assegnazioneTurno.get());
        return true;
    }

    @Override
    public Schedule aggiungiAssegnazioneTurno(RegistraAssegnazioneTurnoDTO assegnazione, boolean forced) throws AssegnazioneTurnoException {
        // Per convertire il dto in un entità ho bisogno di un turno che dovrebbe essere
        // presente nel database
        List<Turno> turni = turnoDao.findAllByServizioNomeAndTipologiaTurno(assegnazione.getServizio().getNome(), assegnazione.getTipologiaTurno());
        if(turni.size() == 0)
            throw new AssegnazioneTurnoException("Non esiste un turno con la coppia di attributi servizio: "+assegnazione.getServizio().getNome() +",tipologia turno: "+assegnazione.getTipologiaTurno().toString());
        Turno turno = null;
        for(Turno turnodb: turni){
            if(turnodb.getMansione().equals(assegnazione.getMansione())){
                turno = turnodb;
                break;
            }
        }
        if(turno == null){
            throw new AssegnazioneTurnoException("Non esiste un turno con la coppia di attributi servizio: "+assegnazione.getServizio().getNome() +",mansione: "+assegnazione.getMansione().toString());
        }
        AssegnazioneTurno assegnazioneTurno = new AssegnazioneTurno(
                LocalDate.of(assegnazione.getAnno(), assegnazione.getMese(), assegnazione.getGiorno()),
                turno,
                MappaUtenti.utenteDTOtoEntity(assegnazione.getUtentiReperibili()),
                MappaUtenti.utenteDTOtoEntity(assegnazione.getUtentiDiGuardia()));


        if(!checkAssegnazioneTurno(assegnazioneTurno)){
            throw new AssegnazioneTurnoException("Collisione tra utenti reperibili e di guardia");
        }

        // Converto il dto in un entità

        return this.aggiungiAssegnazioneTurno(assegnazioneTurno,forced);

    }

    private boolean checkAssegnazioneTurno(AssegnazioneTurno turno) {

        for(Utente utente1: turno.getUtentiDiGuardia()){
            for(Utente utente2: turno.getUtentiReperibili()){
                if (utente1.getId().longValue() == utente2.getId().longValue()){
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Questo metodo modifica un assegnazione turno già esistente. Il suo compito è quello di eliminare
     * la vecchia assegnazione turno, verificare che la nuova assegnazione turno modificata rispetti tuitti  i
     * vincoli e in caso di successo salvarla nel database. Se qualche vincolo invece è violato la vecchia assegnazione
     * che era stata eliminata viene salvata nuovamente nel database.
     * @param modificaAssegnazioneTurnoDTO
     * @return
     */
    @Override
    public Schedule modificaAssegnazioneTurno(ModificaAssegnazioneTurnoDTO modificaAssegnazioneTurnoDTO) {

        AssegnazioneTurno assegnazioneTurnoOld  = assegnazioneTurnoDao.findById(modificaAssegnazioneTurnoDTO.getIdAssegnazione()).get();
        AssegnazioneTurno assegnazioneTurnoNew = assegnazioneTurnoOld.clone();

        //Apporto le modifiche sugli utenti allocati , se necessario
        if(modificaAssegnazioneTurnoDTO.getUtenti_guardia()!= null){
            assegnazioneTurnoNew.setUtentiDiGuardia(new HashSet<>());
            for (long idGuardia: modificaAssegnazioneTurnoDTO.getUtenti_guardia()) {
                assegnazioneTurnoNew.addUtentediGuardia(utenteDao.findById(idGuardia));
            }
        }

        //Apporto le modifiche sugli utenti di riserva , se necessario
        if(modificaAssegnazioneTurnoDTO.getUtenti_reperibili()!=null){
            assegnazioneTurnoNew.setUtentiReperibili(new HashSet<>());
            for (long idReperibile: modificaAssegnazioneTurnoDTO.getUtenti_guardia()) {
                assegnazioneTurnoNew.addUtenteReperibile(utenteDao.findById(idReperibile));
            }

        }

        //rimuovo la vecchia assegnazione e provo ad aggiungere la nuova
        this.rimuoviAssegnazioneTurno(assegnazioneTurnoOld);
        Schedule schedule = this.aggiungiAssegnazioneTurno(assegnazioneTurnoNew, true);

        // Se un vincolo è violato riaggiungo l'assegnazione che avevo in precedenza eliminato
        if (schedule.isIllegal()) {
            assegnazioneTurnoOld.setId(null);
            schedule.getAssegnazioniTurno().add(assegnazioneTurnoOld);
            scheduleDao.flush();
        }

        return schedule;
    }
}
