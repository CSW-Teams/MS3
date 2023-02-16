package org.cswteams.ms3;

import org.cswteams.ms3.config.ApplicationStartup;
import org.cswteams.ms3.control.scheduler.IControllerScheduler;
import org.cswteams.ms3.enums.*;
import org.cswteams.ms3.exception.TurnoException;
import org.cswteams.ms3.exception.UnableToBuildScheduleException;
import org.cswteams.ms3.dao.*;
import org.cswteams.ms3.entity.*;
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
import javax.transaction.Transactional;
import java.util.*;


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

    @Autowired
    private UserCategoryPolicyDao userCategoryPolicyDao;

    @Autowired
    private CategorieDao categorieDao;

    @Before
    public void populateDBTestSchedule() throws TurnoException {
        //Crea turni e servizio
        //CREA LE CATEGORIE DI TIPO STATO (ESCLUSIVE PER I TURNI)
        Categoria categoriaOVER62 = new Categoria("OVER_62", TipoCategoriaEnum.STATO);
        Categoria categoriaIncinta = new Categoria("INCINTA",  TipoCategoriaEnum.STATO);
        Categoria categoriaFerie = new Categoria("IN_FERIE",  TipoCategoriaEnum.STATO);
        Categoria categoriaMalattia = new Categoria("IN_MALATTIA",  TipoCategoriaEnum.STATO);

        categorieDao.saveAndFlush(categoriaOVER62);
        categorieDao.saveAndFlush(categoriaIncinta);
        categorieDao.saveAndFlush(categoriaFerie);
        categorieDao.saveAndFlush(categoriaMalattia);

        Servizio servizio1 = new Servizio("cardiologia");
        servizio1.getMansioni().add(MansioneEnum.REPARTO);
        servizioDao.saveAndFlush(servizio1);
        //Creo utente con categoria incinta
        Utente pregUser = new Utente("Giulia", "Rossi", "GLRRSS******", LocalDate.of(1954, 3, 14), "glrss@gmail.com","", RuoloEnum.SPECIALIZZANDO, AttoreEnum.UTENTE );
        CategoriaUtente ci = new CategoriaUtente(categoriaIncinta, LocalDate.now(), LocalDate.now().plusDays(10));
        categoriaUtenteDao.saveAndFlush(ci);
        pregUser.getStato().add(ci);
        utenteDao.saveAndFlush(pregUser);
        //Creo utente generico
        Utente utente = new Utente("Manuel", "Rossi", "******", LocalDate.of(1997, 3, 14), "salvatimartina97@gmail.com", "",RuoloEnum.SPECIALIZZANDO,AttoreEnum.UTENTE );
        utenteDao.saveAndFlush(utente);
        //Crea turni e servizio
        servizioDao.saveAndFlush(servizio1);
        Turno t1 = new Turno(
                LocalTime.of(20, 0),
                LocalTime.of(8, 0),
                servizio1,
                MansioneEnum.REPARTO,
                TipologiaTurno.NOTTURNO,
                true
               );
        turnoDao.saveAndFlush(t1);
        userCategoryPolicyDao.saveAndFlush(new UserCategoryPolicy(categoriaIncinta, t1, UserCategoryPolicyValue.EXCLUDE));
        userCategoryPolicyDao.saveAndFlush(new UserCategoryPolicy(categoriaOVER62, t1, UserCategoryPolicyValue.EXCLUDE));
        userCategoryPolicyDao.saveAndFlush(new UserCategoryPolicy(categoriaFerie, t1, UserCategoryPolicyValue.EXCLUDE));
        userCategoryPolicyDao.saveAndFlush(new UserCategoryPolicy(categoriaMalattia, t1, UserCategoryPolicyValue.EXCLUDE));


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


