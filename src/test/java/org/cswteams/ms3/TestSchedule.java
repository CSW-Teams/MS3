package org.cswteams.ms3;

import org.cswteams.ms3.config.ApplicationStartup;
import org.cswteams.ms3.control.scheduler.IControllerScheduler;
import org.cswteams.ms3.control.scheduler.UnableToBuildScheduleException;
import org.cswteams.ms3.dao.*;
import org.cswteams.ms3.entity.*;
import org.cswteams.ms3.enums.CategoriaUtentiEnum;
import org.cswteams.ms3.enums.RuoloEnum;
import org.cswteams.ms3.enums.TipologiaTurno;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Profile;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.HashSet;
import javax.transaction.Transactional;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Profile("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD) // The spring application context will be considered "dirty" before each test method, and will be rebuilt. It means that
public class TestSchedule extends AbstractTransactionalJUnit4SpringContextTests {

    @Autowired
    private IControllerScheduler controllerScheduler;

    @Autowired
    private ScheduleDao scheduleDao;

    @Autowired
    private UtenteDao utenteDao;

    @Autowired
    private TurnoDao turnoDao;

    @Autowired
    private ServizioDao servizioDao;

    @Autowired
    private CategoriaUtenteDao categoriaUtenteDao;

    @MockBean
    private ApplicationStartup applicationStartup;

    @Before
    public void populateDBTestSchedule() {
            //Creo utente con categoria incinta
            Utente pregUser = new Utente("Giulia", "Rossi", "GLRRSS******", LocalDate.of(1954, 3, 14), "glrss@gmail.com", RuoloEnum.SPECIALIZZANDO);
            CategoriaUtente categoriaIncinta = new CategoriaUtente(CategoriaUtentiEnum.DONNA_INCINTA, LocalDate.now(), LocalDate.now().plusDays(10));
            categoriaUtenteDao.saveAndFlush(categoriaIncinta);
            pregUser.getCategorie().add(categoriaIncinta);
            utenteDao.saveAndFlush(pregUser);
            //Creo utente generico
            Utente utente = new Utente("Manuel", "Rossi", "******", LocalDate.of(1997, 3, 14), "salvatimartina97@gmail.com", RuoloEnum.SPECIALIZZANDO);
            utenteDao.saveAndFlush(utente);
            //Crea turni e servizio
            Servizio servizio1 = new Servizio("reparto");
            servizioDao.saveAndFlush(servizio1);
            Turno t1 = new Turno(
                    LocalTime.of(20, 0),
                    LocalTime.of(23, 0),
                    servizio1,
                    TipologiaTurno.NOTTURNO,
                    new HashSet<>(Arrays.asList(
                            CategoriaUtentiEnum.DONNA_INCINTA,
                            CategoriaUtentiEnum.OVER_62,
                            CategoriaUtentiEnum.IN_MALATTIA,
                            CategoriaUtentiEnum.IN_FERIE)
                    ));

            t1.setNumUtentiGuardia(1);
            turnoDao.saveAndFlush(t1);
    }

    @Test
    @Transactional
    /** needed to avoid "org.hibernate.LazyInitializationException: could not initialize proxy - no Session".
     *  Remember to flush daos after each change to entities */
    public void TestScheduler() throws UnableToBuildScheduleException {

        LocalDate startDate = LocalDate.now();
        LocalDate endDate = startDate.plusDays(5);

        Long scheduleId = this.controllerScheduler.createSchedule(startDate, endDate).getId();

        Schedule schedule = scheduleDao.findById(scheduleId).get();
        //Verifico che il numero di utenti nel turno sia 1
        Assert.assertEquals(schedule.getAssegnazioniTurno().get(0).getUtenti().size(), 1);
        //Verifico che la donna incinta non è stata inserita nel turno notturno
        Assert.assertNotEquals(schedule.getAssegnazioniTurno().get(0).getUtentiAsList().get(0).getNome(), "Giulia");
        Assert.assertEquals(schedule.getAssegnazioniTurno().get(0).getUtentiAsList().get(0).getNome(), "Manuel");
    }
}


