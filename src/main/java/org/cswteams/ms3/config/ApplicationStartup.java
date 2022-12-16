package org.cswteams.ms3.config;

import lombok.SneakyThrows;
import org.cswteams.ms3.dao.AssegnazioneTurnoDao;
import org.cswteams.ms3.dao.TurnoDao;
import org.cswteams.ms3.dao.UtenteDao;
import org.cswteams.ms3.entity.AssegnazioneTurno;
import org.cswteams.ms3.enums.RuoloEnum;
import org.cswteams.ms3.enums.Servizio;
import org.cswteams.ms3.enums.TipologiaTurno;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import org.cswteams.ms3.entity.Turno;
import org.cswteams.ms3.entity.Utente;
import java.time.*;
import java.time.temporal.*;
import java.util.HashSet;
import java.util.Set;

@Component()
public class ApplicationStartup implements ApplicationListener<ApplicationReadyEvent> {

    /**
     * This event is executed as late as conceivably possible to indicate that
     * the application is ready to service requests.
     */

    @Autowired
    private UtenteDao utenteDao;

    @Autowired
    private AssegnazioneTurnoDao assegnazioneTurnoDao;

    @Autowired
    private TurnoDao turnoDao;


    @SneakyThrows
    @Override
    public void onApplicationEvent(ApplicationReadyEvent applicationReadyEvent) {
        Utente u1 = new Utente("Martina","Salvati", "SLVMTN******", LocalDate.of(1997, 3, 14),"salvatimartina97@gmail.com", RuoloEnum.SPECIALIZZANDO );
        Utente u2 = new Utente("Domenico","Verde", "DMNCVRD******", LocalDate.of(1997, 5, 23),"domenicoverde@gmail.com", RuoloEnum.SPECIALIZZANDO);
        Utente u3 = new Utente("Federica","Villani", "FDRVLLN******", LocalDate.of(1998, 2, 12),"federicavillani@gmail.com", RuoloEnum.SPECIALIZZANDO);
        Utente u4 = new Utente("Daniele","Colavecchi", "DNLCLV******", LocalDate.of(1982, 7, 6),"danielecolavecchi@gmail.com", RuoloEnum.STRUTTURATO);
        Utente u5 = new Utente("Daniele","La Prova", "DNLLPRV******", LocalDate.of(1998, 2, 12),"danielelaprova@gmail.com", RuoloEnum.STRUTTURATO);
        Utente u6 = new Utente("Luca","Fiscariello", "FSCRLC******", LocalDate.of(1998, 8, 12),"lucafiscariello",RuoloEnum.STRUTTURATO);

        u1 = utenteDao.save(u1);
        u2 = utenteDao.save(u2);
        u3 = utenteDao.save(u3);
        u4 = utenteDao.save(u4);
        u5 = utenteDao.save(u5);
        u6 = utenteDao.save(u6);

        Turno t1 = new Turno(LocalTime.of(8,0), LocalTime.of(14, 0), Servizio.REPARTO, TipologiaTurno.MATTUTINO);
        Turno t2 = new Turno(LocalTime.of(14,0), LocalTime.of(20, 0), Servizio.REPARTO, TipologiaTurno.POMERIDIANO);
        Turno t3 = new Turno(LocalTime.of(20,0), LocalTime.of(23, 0), Servizio.REPARTO, TipologiaTurno.NOTTURNO);
        Turno t4 = new Turno(LocalTime.of(0,0), LocalTime.of(8, 0), Servizio.REPARTO, TipologiaTurno.NOTTURNO);

        t1 = turnoDao.save(t1);
        t2 = turnoDao.save(t2);
        t3 = turnoDao.save(t3);
        t4 = turnoDao.save(t4);

        Set<Utente> setUtentiGuardia1 = new HashSet<>();
        setUtentiGuardia1.add(u1);
        setUtentiGuardia1.add(u2);
        setUtentiGuardia1.add(u4);
        setUtentiGuardia1.add(u5);

        Set<Utente> setUtentiReperibili1 = new HashSet<>();
        setUtentiGuardia1.add(u3);
        setUtentiGuardia1.add(u6);

        Set<Utente> setUtentiGuardia2 = new HashSet<>();
        setUtentiGuardia1.add(u3);
        setUtentiGuardia1.add(u6);

        Set<Utente> setUtentiReperibili2 = new HashSet<>();
        setUtentiGuardia1.add(u1);
        setUtentiGuardia1.add(u4);

        AssegnazioneTurno turnoassegnato1 =  new AssegnazioneTurno(LocalDate.of(2022,12,15),t1,setUtentiGuardia1,setUtentiGuardia1);
        AssegnazioneTurno turnoassegnato2 =  new AssegnazioneTurno(LocalDate.of(2022,12,15),t2,setUtentiGuardia1,setUtentiReperibili1);
        AssegnazioneTurno turnoassegnato3 =  new AssegnazioneTurno(LocalDate.of(2022,12,15),t3,setUtentiGuardia2,setUtentiReperibili2);
        AssegnazioneTurno turnoassegnato4 =  new AssegnazioneTurno(LocalDate.of(2022,12,15),t4,setUtentiGuardia2,setUtentiReperibili2);

        turnoassegnato1 = assegnazioneTurnoDao.save(turnoassegnato1);
        turnoassegnato2 = assegnazioneTurnoDao.save(turnoassegnato2);
        turnoassegnato3 = assegnazioneTurnoDao.save(turnoassegnato3);
        turnoassegnato4 = assegnazioneTurnoDao.save(turnoassegnato4);


    }
}