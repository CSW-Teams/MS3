package org.cswteams.ms3;

import org.cswteams.ms3.entity.*;
import org.cswteams.ms3.entity.vincoli.ContestoVincolo;
import org.cswteams.ms3.entity.vincoli.Vincolo;
import org.cswteams.ms3.entity.vincoli.VincoloUbiquità;
import org.cswteams.ms3.enums.AttoreEnum;
import org.cswteams.ms3.enums.MansioneEnum;
import org.cswteams.ms3.enums.RuoloEnum;
import org.cswteams.ms3.enums.TipologiaTurno;
import org.cswteams.ms3.exception.TurnoException;
import org.cswteams.ms3.exception.ViolatedConstraintException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.transaction.Transactional;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD) // The spring application context will be considered "dirty" before each test method, and will be rebuilt. It means that
@Transactional
public class VincoloUbiquitàTest {

    @Test(expected= ViolatedConstraintException.class)
    /**Test che verifica che un utente non può effettuare più di un tot ore consecutive */
    public void turniAccavallatiTest() throws ViolatedConstraintException, TurnoException{
        Servizio servizio1 = new Servizio("cardiologia");
        servizio1.getMansioni().addAll(Arrays.asList(MansioneEnum.REPARTO, MansioneEnum.AMBULATORIO));
        //Servizio servizio2 = new Servizio("ambulatorio");
        Turno t1 = new Turno(LocalTime.of(8, 0), Duration.ofHours(6), servizio1,  MansioneEnum.REPARTO,TipologiaTurno.MATTUTINO,true);
        Turno t2 = new Turno(LocalTime.of(10, 0), Duration.ofHours(2), servizio1,  MansioneEnum.AMBULATORIO, TipologiaTurno.MATTUTINO,true);

        Utente utente = new Utente("Giulia","Rossi", "GLRRSS******", LocalDate.of(1999, 3, 14),"glrss@gmail.com", "",RuoloEnum.SPECIALIZZANDO, AttoreEnum.UTENTE  );

        AssegnazioneTurno turnoAssegnato = new AssegnazioneTurno(LocalDate.of(2023,1, 10),t1,new HashSet<>(),new HashSet<>(Collections.singletonList(utente)));
        AssegnazioneTurno turnoDaAssegnare = new AssegnazioneTurno(LocalDate.of(2023,1, 10),t2,new HashSet<>(),new HashSet<>(Collections.singletonList(utente)));

        Schedule scheduleTest = new Schedule(LocalDate.of(2023,1,10),LocalDate.of(2023,1,17));
        scheduleTest.setAssegnazioniTurno(new ArrayList<>(List.of(turnoAssegnato)));

        UserScheduleState pregUserState = new UserScheduleState(utente, scheduleTest);

        Vincolo vincoloUbiquità = new VincoloUbiquità();

        vincoloUbiquità.verificaVincolo(new ContestoVincolo(pregUserState,turnoDaAssegnare));
    }

    @Test(expected= ViolatedConstraintException.class)
    /**Test che verifica che un utente non può effettuare più di un tot ore consecutive */
    public void turniCoincidentiTest() throws ViolatedConstraintException, TurnoException{
        Servizio servizio1 = new Servizio("cardiologia");
        servizio1.getMansioni().addAll(Arrays.asList(MansioneEnum.REPARTO, MansioneEnum.AMBULATORIO));
        Servizio servizio2 = new Servizio("cardiologia");
        servizio2.getMansioni().addAll(Arrays.asList(MansioneEnum.REPARTO, MansioneEnum.AMBULATORIO));
        Turno t1 = new Turno(LocalTime.of(8, 0), Duration.ofHours(6), servizio1, MansioneEnum.REPARTO, TipologiaTurno.MATTUTINO,true);
        Turno t2 = new Turno(LocalTime.of(8, 0), Duration.ofHours(6), servizio2, MansioneEnum.REPARTO, TipologiaTurno.MATTUTINO,true);

        Utente utente = new Utente("Giulia","Rossi", "GLRRSS******", LocalDate.of(1999, 3, 14),"glrss@gmail.com", "",RuoloEnum.SPECIALIZZANDO,AttoreEnum.UTENTE  );

        AssegnazioneTurno turnoAssegnato = new AssegnazioneTurno(LocalDate.of(2023,1, 10),t1,new HashSet<>(),new HashSet<>(Collections.singletonList(utente)));
        AssegnazioneTurno turnoDaAssegnare = new AssegnazioneTurno(LocalDate.of(2023,1, 10),t2,new HashSet<>(),new HashSet<>(Collections.singletonList(utente)));

        Schedule scheduleTest = new Schedule(LocalDate.of(2023,1,10),LocalDate.of(2023,1,17));
        scheduleTest.setAssegnazioniTurno(new ArrayList<>(List.of(turnoAssegnato)));

        UserScheduleState pregUserState = new UserScheduleState(utente, scheduleTest);

        Vincolo vincoloUbiquità = new VincoloUbiquità();

        vincoloUbiquità.verificaVincolo(new ContestoVincolo(pregUserState,turnoDaAssegnare));
    }
}
