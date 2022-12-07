package org.cswteams.ms3.config;

import org.cswteams.ms3.control.turni.ControllerTurni;
import org.cswteams.ms3.dao.RuoloDao;
import org.cswteams.ms3.dao.TurnoDao;
import org.cswteams.ms3.dao.UtenteDao;
import org.cswteams.ms3.entity.Ruolo;
import org.cswteams.ms3.entity.Turno;
import org.cswteams.ms3.entity.Utente;
import org.cswteams.ms3.enums.RuoloEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;


@Component()
public class ApplicationStartup implements ApplicationListener<ApplicationReadyEvent> {

    /**
     * This event is executed as late as conceivably possible to indicate that
     * the application is ready to service requests.
     */

    @Autowired
    private RuoloDao ruoloDao;

    @Autowired
    private UtenteDao utenteDao;

    @Autowired
    private TurnoDao turnoDao;

    @Override
    public void onApplicationEvent(ApplicationReadyEvent applicationReadyEvent) {
        Ruolo r1 = new Ruolo(RuoloEnum.STRUTTURATO);
        Ruolo r2 = new Ruolo(RuoloEnum.SPECIALIZZANDO);

        Utente u1 = new Utente("Giovanni","Cantone", r1);
        Utente u2 = new Utente("Manuel","Mastrofini", r2);

        u1 = utenteDao.save(u1);
        u2 = utenteDao.save(u2);

        Timestamp inizio = Timestamp.valueOf("2022-07-11 08:30:00");
        Timestamp fine = Timestamp.valueOf("2022-07-11 13:30:00");

        List<Utente> utenti =  new ArrayList<Utente>();
        utenti.add(u1);
        utenti.add(u2);
        Turno t1 = new Turno(inizio,fine,utenti);
        t1 = turnoDao.save(t1);

    }
}