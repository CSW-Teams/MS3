package org.cswteams.ms3;

import org.cswteams.ms3.entity.vincoli.ContestoVincolo;
import org.cswteams.ms3.entity.vincoli.Vincolo;
import org.cswteams.ms3.entity.vincoli.VincoloMaxOrePeriodo;
import org.cswteams.ms3.dao.AssegnazioneTurnoDao;
import org.cswteams.ms3.dao.ServizioDao;
import org.cswteams.ms3.dao.TurnoDao;
import org.cswteams.ms3.dao.UtenteDao;
import org.cswteams.ms3.entity.*;
import org.cswteams.ms3.enums.CategoriaUtentiEnum;
import org.cswteams.ms3.enums.RuoloEnum;
import org.cswteams.ms3.enums.TipologiaTurno;
import org.cswteams.ms3.exception.TurnoException;
import org.cswteams.ms3.exception.ViolatedConstraintException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD) // The spring application context will be considered "dirty" before each test method, and will be rebuilt. It means that
@Transactional
public class VincoloMaxOrePeriodoTest {

    @Autowired
    private UtenteDao utenteDao;

    @Autowired
    private AssegnazioneTurnoDao assegnazioneTurnoDao;

    @Autowired
    private TurnoDao turnoDao;

    @Autowired
    private ServizioDao servizioDao;

    @Test(expected= ViolatedConstraintException.class)
    /**Test che verifica che un utente non può effettuare più di un tot ore consecutive */
    public void maxOrePeriodoTest() throws ViolatedConstraintException, TurnoException {
        //Crea turni e servizio
        Servizio servizio1 = new Servizio("reparto");
        servizioDao.save(servizio1);
        Turno t1 = new Turno(LocalTime.of(8, 0), LocalTime.of(14, 0), servizio1, TipologiaTurno.MATTUTINO, new HashSet<>(Arrays.asList(CategoriaUtentiEnum.IN_MALATTIA, CategoriaUtentiEnum.IN_FERIE)),false);
        Turno t2 = new Turno(LocalTime.of(14, 0), LocalTime.of(20, 0), servizio1, TipologiaTurno.POMERIDIANO, new HashSet<>(Arrays.asList(CategoriaUtentiEnum.IN_MALATTIA, CategoriaUtentiEnum.IN_FERIE)),false);
        Turno t3 = new Turno(LocalTime.of(20, 0), LocalTime.of(8, 0), servizio1, TipologiaTurno.NOTTURNO, new HashSet<>(Arrays.asList(CategoriaUtentiEnum.DONNA_INCINTA, CategoriaUtentiEnum.OVER_62, CategoriaUtentiEnum.IN_MALATTIA, CategoriaUtentiEnum.IN_FERIE)),true);
        //Turno t4 = new Turno(LocalTime.of(0, 0), LocalTime.of(8, 0), servizio1, TipologiaTurno.NOTTURNO, new HashSet<>(Arrays.asList(CategoriaUtentiEnum.DONNA_INCINTA, CategoriaUtentiEnum.OVER_62, CategoriaUtentiEnum.IN_MALATTIA, CategoriaUtentiEnum.IN_FERIE)));
        turnoDao.save(t1);
        turnoDao.save(t2);
        turnoDao.save(t3);
        //turnoDao.save(t4);
        //Crea utente
        Utente utente = new Utente("Giulia","Rossi", "GLRRSS******", LocalDate.of(1999, 3, 14),"glrss@gmail.com", RuoloEnum.SPECIALIZZANDO );
        utenteDao.save(utente);

        //Aggiungi assegnazione turno
        AssegnazioneTurno turnoNotturno1 = new AssegnazioneTurno(LocalDate.of(2023,1, 10),t3,new HashSet<>(),new HashSet<>(Collections.singletonList(utente)));
        AssegnazioneTurno turnoNotturno2 = new AssegnazioneTurno(LocalDate.of(2023,1, 11),t3,new HashSet<>(),new HashSet<>(Collections.singletonList(utente)));
        AssegnazioneTurno turnoNotturno3 = new AssegnazioneTurno(LocalDate.of(2023,1, 12),t3,new HashSet<>(),new HashSet<>(Collections.singletonList(utente)));
        AssegnazioneTurno turnoNotturno4 = new AssegnazioneTurno(LocalDate.of(2023,1, 13),t3,new HashSet<>(),new HashSet<>(Collections.singletonList(utente)));
        AssegnazioneTurno turnoNotturno5 = new AssegnazioneTurno(LocalDate.of(2023,1, 14),t3,new HashSet<>(),new HashSet<>(Collections.singletonList(utente)));

        assegnazioneTurnoDao.save(turnoNotturno1);
        assegnazioneTurnoDao.save(turnoNotturno2);
        assegnazioneTurnoDao.save(turnoNotturno3);
        assegnazioneTurnoDao.save(turnoNotturno4);
        assegnazioneTurnoDao.save(turnoNotturno5);

        Schedule scheduleTest = new Schedule(LocalDate.of(2023,1,10),LocalDate.of(2023,1,17));
        scheduleTest.setAssegnazioniTurno(new ArrayList<>(Arrays.asList(turnoNotturno1,turnoNotturno2,turnoNotturno3,turnoNotturno4,turnoNotturno5)));

        UserScheduleState pregUserState = new UserScheduleState(utente, scheduleTest);

        Vincolo vincoloMaxOrePeriodo = new VincoloMaxOrePeriodo(7,60*60);

        vincoloMaxOrePeriodo.verificaVincolo(new ContestoVincolo(pregUserState,new AssegnazioneTurno(LocalDate.of(2023,1, 15),t3,new HashSet<>(),new HashSet<>(Collections.singletonList(utente)))));
    }

}
