package org.cswteams.ms3.control.scheduler;

import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;

import org.cswteams.ms3.dao.ServizioDao;
import org.cswteams.ms3.dao.TurnoDao;
import org.cswteams.ms3.dao.UtenteDao;
import org.cswteams.ms3.entity.AssegnazioneTurno;
import org.cswteams.ms3.entity.Schedule;
import org.cswteams.ms3.entity.Turno;
import org.cswteams.ms3.dao.VincoloGenericoDao;
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

    @Autowired
    VincoloGenericoDao vincoloDao;

    ScheduleBuilder scheduleBuilder;

    public Schedule createSchedule(LocalDate startDate, LocalDate endDate) throws UnableToBuildScheduleException{

        //Data che uso per scorre l'intervallo di giorni
        LocalDate currentDay = startDate;
        List<AssegnazioneTurno> allAssegnazioni = new ArrayList<>();

        //Scorro tutti i giorni dell'intervallo. Per ogni giorno scorro tutti i turni.
        while(!currentDay.isAfter(endDate)){
            for(Turno turno : turnoDao.findAll()){
                AssegnazioneTurno assegnazione = new AssegnazioneTurno(currentDay,turno);
                allAssegnazioni.add(assegnazione);
            }

            //Mi sposto al giorno seguente.
            currentDay = currentDay.plusDays(1);
        }

        //Creo uno schedule builder ad ogni nuova pianificazione
        this.scheduleBuilder = new ScheduleBuilder(startDate,endDate,vincoloDao.findAll(), allAssegnazioni, utenteDao.findAll());
        return this.scheduleBuilder.build();
        
    }

    

    
}
