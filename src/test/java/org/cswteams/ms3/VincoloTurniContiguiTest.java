package org.cswteams.ms3;

import org.cswteams.ms3.entity.vincoli.ContestoVincolo;
import org.cswteams.ms3.entity.vincoli.Vincolo;
import org.cswteams.ms3.entity.vincoli.VincoloTipologieTurniContigue;
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
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@Transactional
public class VincoloTurniContiguiTest {

    @Autowired
    private UtenteDao utenteDao;

    @Autowired
    private AssegnazioneTurnoDao assegnazioneTurnoDao;

    @Autowired
    private TurnoDao turnoDao;

    @Autowired
    private ServizioDao servizioDao;

    @Test(expected= ViolatedConstraintException.class)
    public void testTurniContigui() throws ViolatedConstraintException, TurnoException {
        //Crea turni e servizio
        Servizio servizio1 = new Servizio("reparto");
        servizioDao.saveAndFlush(servizio1);
        
        HashSet<CategoriaUtentiEnum> categorieVietate= new HashSet<>(Arrays.asList(
                CategoriaUtentiEnum.DONNA_INCINTA,
                CategoriaUtentiEnum.OVER_62,
                CategoriaUtentiEnum.IN_MALATTIA,
                CategoriaUtentiEnum.IN_FERIE)
        );
        
        Turno t3 = new Turno(LocalTime.of(20, 0), LocalTime.of(5, 0), servizio1, TipologiaTurno.NOTTURNO, categorieVietate,true);
        t3.setNumUtentiGuardia(1);
        t3.setNumUtentiReperibilita(1);

        Turno t5 = new Turno(LocalTime.of(10, 0), LocalTime.of(12, 0), servizio1, TipologiaTurno.MATTUTINO, new HashSet<>(),false);
        t5.setNumUtentiGuardia(1);
        t5.setNumUtentiReperibilita(1);
        
        turnoDao.saveAndFlush(t3);
        turnoDao.saveAndFlush(t5);
        //Crea utente
        Utente utente = new Utente("Giulia","Rossi", "GLRRSS******", LocalDate.of(1999, 3, 14),"glrss@gmail.com", RuoloEnum.SPECIALIZZANDO );
        utenteDao.saveAndFlush(utente);

        //Aggiungi assegnazione turno
        AssegnazioneTurno turnoNotte = new AssegnazioneTurno(LocalDate.of(2023,1, 10),t3,new HashSet<>(),new HashSet<>(Collections.singletonList(utente)));
        AssegnazioneTurno turnoMattina = new AssegnazioneTurno(LocalDate.of(2023,1, 11),t5,new HashSet<>(),new HashSet<>());
        assegnazioneTurnoDao.save(turnoNotte);
        assegnazioneTurnoDao.save(turnoMattina);

        Schedule scheduleTest = new Schedule();
        scheduleTest.setAssegnazioniTurno(new ArrayList<>(Collections.singletonList(turnoNotte)));

        UserScheduleState usstate = new UserScheduleState(utente, scheduleTest);

        Vincolo vincoloTurniContigui = new VincoloTipologieTurniContigue(
            20,
            ChronoUnit.HOURS,
            TipologiaTurno.NOTTURNO,
            new HashSet<>(Arrays.asList(TipologiaTurno.values()))); // nessun turno può essere allocato a questa persona durante il suo smonto notte
        vincoloTurniContigui.verificaVincolo(new ContestoVincolo(usstate,turnoMattina));
    }
}
