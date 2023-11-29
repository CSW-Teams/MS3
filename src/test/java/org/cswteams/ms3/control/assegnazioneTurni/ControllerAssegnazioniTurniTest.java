package org.cswteams.ms3.control.assegnazioneTurni;


import org.cswteams.ms3.control.assegnazioneTurni.IControllerAssegnazioneTurni;
import org.cswteams.ms3.dao.AssegnazioneTurnoDao;
import org.cswteams.ms3.dao.ServizioDao;
import org.cswteams.ms3.dao.TurnoDao;
import org.cswteams.ms3.dao.UtenteDao;
import org.cswteams.ms3.dto.AssegnazioneTurnoDTO;
import org.cswteams.ms3.entity.AssegnazioneTurno;
import org.cswteams.ms3.entity.Servizio;
import org.cswteams.ms3.entity.Turno;
import org.cswteams.ms3.entity.Utente;
import org.cswteams.ms3.enums.AttoreEnum;
import org.cswteams.ms3.enums.MansioneEnum;
import org.cswteams.ms3.enums.RuoloEnum;
import org.cswteams.ms3.enums.TipologiaTurno;
import org.cswteams.ms3.exception.TurnoException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.transaction.Transactional;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@Profile("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD) // The spring application context will be considered "dirty" before each test method, and will be rebuilt. It means that
@Transactional
public class ControllerAssegnazioniTurniTest {
    @Autowired
    private ServizioDao servizioDao;
    @Autowired
    private TurnoDao turnoDao;
    @Autowired
    private UtenteDao utenteDao;
    @Autowired
    private AssegnazioneTurnoDao assegnazioneTurnoDao;
    @Autowired
    private IControllerAssegnazioneTurni controllerAssegnazioneTurni;
    @Test
    /**
     * Test che verifica che dai turni assegnati non vengono messi letti anche i turni non assegnati
     */
    public void leggiTurniAssegnati() throws TurnoException, ParseException{
        Servizio servizio1 = new Servizio("cardiologia");
        servizio1.getMansioni().add(MansioneEnum.REPARTO);
        servizioDao.save(servizio1);
        Turno t1 = new Turno(LocalTime.of(8, 0), LocalTime.of(14, 0), servizio1, MansioneEnum.REPARTO,  TipologiaTurno.MATTUTINO,true);
        Turno t2 = new Turno(LocalTime.of(14, 0), LocalTime.of(20, 0), servizio1, MansioneEnum.REPARTO,  TipologiaTurno.POMERIDIANO,true);
        Turno t3 = new Turno(LocalTime.of(20, 0), LocalTime.of(8, 0), servizio1, MansioneEnum.REPARTO,  TipologiaTurno.NOTTURNO,true);
        turnoDao.save(t1);
        turnoDao.save(t2);
        turnoDao.save(t3);
        Utente utente = new Utente("Martina", "Salvati", "SLVMTN******", LocalDate.of(1997, 3, 14), "salvatimartina97@gmail.com", "passw", RuoloEnum.SPECIALIZZANDO, AttoreEnum.UTENTE);
        this.utenteDao.saveAndFlush(utente);
        Utente utente2 = new Utente("Matteo", "Federico", "FDRMTT******", LocalDate.of(1998, 11, 20), "matteofederico@gmail.com", "passw", RuoloEnum.SPECIALIZZANDO, AttoreEnum.UTENTE);
        this.utenteDao.saveAndFlush(utente2);
        AssegnazioneTurno turnoPomeriggio = new AssegnazioneTurno(LocalDate.of(2023,1, 10),t2,new HashSet<>(),new HashSet<>(Collections.singletonList(utente)));
        AssegnazioneTurno turnoNotturno = new AssegnazioneTurno(LocalDate.of(2023,1, 10),t3,new HashSet<>(),new HashSet<>(Collections.singletonList(utente2)));
        assegnazioneTurnoDao.saveAndFlush(turnoPomeriggio);
        assegnazioneTurnoDao.saveAndFlush(turnoNotturno);
        Set<AssegnazioneTurnoDTO> set = controllerAssegnazioneTurni.leggiTurniAssegnati();
        for (AssegnazioneTurnoDTO e:set){
            Assert.assertNotEquals(e.getIdTurno(),t1.getId());
        }
    }

    @Test
    /**
     * Test verifica che il turno venga assegnato al giusto utente
     */
    public void leggiTurniUtente() throws TurnoException, ParseException {
        Utente utente = new Utente("Martina", "Salvati", "SLVMTN******", LocalDate.of(1997, 3, 14), "salvatimartina97@gmail.com", "passw", RuoloEnum.SPECIALIZZANDO, AttoreEnum.UTENTE);
        utente=this.utenteDao.saveAndFlush(utente);
        Servizio servizio1 = new Servizio("cardiologia");
        servizio1.getMansioni().add(MansioneEnum.REPARTO);
        servizioDao.save(servizio1);
        Turno t1 = new Turno(LocalTime.of(8, 0), LocalTime.of(14, 0), servizio1, MansioneEnum.REPARTO,  TipologiaTurno.MATTUTINO,true);
        t1=turnoDao.save(t1);
        AssegnazioneTurno turnoPomeriggio = new AssegnazioneTurno(LocalDate.of(2023,1, 10),t1,new HashSet<>(),new HashSet<>(Collections.singletonList(utente)));
        assegnazioneTurnoDao.saveAndFlush(turnoPomeriggio);
        Set<AssegnazioneTurnoDTO> setTurni = controllerAssegnazioneTurni.leggiTurniUtente(utente.getId());
        for (AssegnazioneTurnoDTO app:setTurni){
           Assert.assertEquals(app.getIdTurno(),t1.getId());
        }
    }
}