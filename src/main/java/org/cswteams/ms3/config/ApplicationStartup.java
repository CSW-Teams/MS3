package org.cswteams.ms3.config;

import org.cswteams.ms3.dao.RuoloDao;
import org.cswteams.ms3.dao.AssegnazioneTurnoDao;
import org.cswteams.ms3.dao.UtenteDao;
import org.cswteams.ms3.entity.Ruolo;
import org.cswteams.ms3.entity.AssegnazioneTurno;
import org.cswteams.ms3.entity.Utente;
import org.cswteams.ms3.enums.RuoloEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
    private AssegnazioneTurnoDao turnoDao;

    @Override
    public void onApplicationEvent(ApplicationReadyEvent applicationReadyEvent) {
        Ruolo r1 = new Ruolo(RuoloEnum.STRUTTURATO);
        Ruolo r2 = new Ruolo(RuoloEnum.SPECIALIZZANDO);
        Ruolo r3 = new Ruolo(RuoloEnum.SPECIALIZZANDO);


        Utente u1 = new Utente("Giovanni","Cantone", r1);
        Utente u2 = new Utente("Manuel","Mastrofini", r2);
        Utente u3 = new Utente("Antonio","Altieri", r3);

        u1 = utenteDao.save(u1);
        u2 = utenteDao.save(u2);
        utenteDao.save(u3);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        Date dataInizio;
        Date datFine;

        try {
            dataInizio = sdf.parse("2022-12-10T11:30:00");
            datFine= sdf.parse("2022-12-10T14:30:00");
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        Timestamp inizio = new Timestamp(dataInizio.getTime());
        Timestamp fine = new Timestamp(datFine.getTime());

        List<Utente> utenti =  new ArrayList<Utente>();
        utenti.add(u1);
        utenti.add(u2);
        //AssegnazioneTurno t1 = new AssegnazioneTurno(inizio,fine,utenti);
        //t1 = turnoDao.save(t1);

    }
}