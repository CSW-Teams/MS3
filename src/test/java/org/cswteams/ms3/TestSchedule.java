package org.cswteams.ms3;

import org.cswteams.ms3.control.scheduler.IControllerScheduler;
import org.cswteams.ms3.control.scheduler.UnableToBuildScheduleException;
import org.cswteams.ms3.dao.*;
import org.cswteams.ms3.entity.*;
import org.cswteams.ms3.enums.CategoriaUtentiEnum;
import org.cswteams.ms3.enums.RuoloEnum;
import org.cswteams.ms3.enums.TipologiaTurno;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.HashSet;

import javax.transaction.Transactional;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
public class TestSchedule {

    @Autowired
    private UtenteDao utenteDao;

    @Autowired
    private ServizioDao servizioDao;

    @Autowired
    private TurnoDao turnoDao;

    @Autowired
    private IControllerScheduler controllerScheduler;

    @Autowired
    private CategoriaUtenteDao categoriaUtenteDao;

    @Autowired
    private ScheduleDao scheduleDao;

    @Test
    /** needed to avoid "org.hibernate.LazyInitializationException: could not initialize proxy - no Session".
     *  Remember to flush daos after each change to entities */
    @Transactional 
    public void TestNotValid() throws UnableToBuildScheduleException{

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
        
        //Crea utente - donna INCINTA
        Utente pregUser = new Utente("Giulia","Rossi", "GLRRSS******", LocalDate.of(1954, 3, 14),"glrss@gmail.com", RuoloEnum.SPECIALIZZANDO );
        Utente utente = new Utente("Antonio","Rossi", "GLRRSS******", LocalDate.of(1955, 3, 14),"antss@gmail.com", RuoloEnum.SPECIALIZZANDO );

        CategoriaUtente categoriaIncinta = new CategoriaUtente(CategoriaUtentiEnum.DONNA_INCINTA,LocalDate.now(), LocalDate.now().plusDays(10));
        categoriaUtenteDao.saveAndFlush(categoriaIncinta);
        
        pregUser.getCategorie().add(categoriaIncinta);
        utenteDao.saveAndFlush(pregUser);
        utenteDao.saveAndFlush(utente);


        LocalDate startDate = LocalDate.now();
        LocalDate endDate = startDate.plusDays(5);
        
        long scheduleId = this.controllerScheduler.createSchedule(startDate,endDate).getId();

        Schedule schedule = scheduleDao.findById(scheduleId).get();

        assertEquals(schedule.getAssegnazioniTurno().get(0).getUtenti().size(), 1);
        assertEquals(schedule.getAssegnazioniTurno().get(0).getUtentiAsList().get(0).getNome(), utente.getNome());

        

}

    


}
