package org.cswteams.ms3.control.scheduler;

import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;

import org.cswteams.ms3.control.vincoli.Vincolo;
import org.cswteams.ms3.control.vincoli.VincoloPersonaTurno;
import org.cswteams.ms3.dao.ServizioDao;
import org.cswteams.ms3.dao.TurnoDao;
import org.cswteams.ms3.dao.UtenteDao;
import org.cswteams.ms3.entity.AssegnazioneTurno;
import org.cswteams.ms3.entity.Schedule;
import org.cswteams.ms3.entity.Turno;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class ControllerScheduler implements IControllerScheduler{

    @Autowired
    ServizioDao servizioDao;

    @Autowired
    UtenteDao utenteDao;

    @Autowired
    TurnoDao turnoDao;



    ScheduleBuilder scheduleBuilder;

  /** 
     * Restituisce tutti i vincoli di tutte le categorie, instanziando quelli
     * stateless e ritrovando le istanze stateful dal database contattando le
     * opportune dao.
     */
    private List<Vincolo> getAllConstraints(){
        List<Vincolo> vincoli = new ArrayList<>();
        
        vincoli.add(new VincoloPersonaTurno());

        // aggiungere altri vincoli ...

        return vincoli;
    }
    
    @Override
    public Schedule createSchedule(LocalDate startDate, LocalDate endDate) throws UnableToBuildScheduleException{

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
            getAllConstraints(),    // tutti i vincoli da rispettare quando si assegna una persona a un turno
            allAssegnazioni,    // assegnazioni di turno con data (senza partecipanti)
            utenteDao.findAll() // tutti i candidati da allocare ai turni
            );
        return this.scheduleBuilder.build();
        
    }

    

    
}
