package org.cswteams.ms3;

import org.cswteams.ms3.dao.*;
import org.cswteams.ms3.entity.doctor.Doctor;
import org.cswteams.ms3.entity.policy.ConditionPolicy;
import org.cswteams.ms3.entity.vincoli.ContestoVincolo;
import org.cswteams.ms3.entity.vincoli.Vincolo;
import org.cswteams.ms3.entity.vincoli.VincoloTipologieTurniContigue;
import org.cswteams.ms3.entity.*;
import org.cswteams.ms3.enums.*;
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
import java.time.Duration;
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

    @Autowired
    private UserCategoryPolicyDao userCategoryPolicyDao;

    @Autowired
    private ConditionDao conditionDao;

    @Test(expected= ViolatedConstraintException.class)
    public void testTurniContigui() throws ViolatedConstraintException, TurnoException {
        //Crea turni e servizio
        //CREA LE CATEGORIE DI TIPO STATO (ESCLUSIVE PER I TURNI)
        Categoria categoriaOVER62 = new Categoria("OVER_62", TipoCategoriaEnum.STATO);
        Categoria categoriaIncinta = new Categoria("INCINTA", TipoCategoriaEnum.STATO);
        Categoria categoriaFerie = new Categoria("IN_FERIE", TipoCategoriaEnum.STATO);
        Categoria categoriaMalattia = new Categoria("IN_MALATTIA", TipoCategoriaEnum.STATO);
        conditionDao.save(categoriaFerie);
        conditionDao.save(categoriaIncinta);
        conditionDao.save(categoriaMalattia);
        conditionDao.save(categoriaOVER62);
        Servizio servizio1 = new Servizio("cardiologia");
        servizio1.getMansioni().add(MansioneEnum.REPARTO);
        servizio1.getMansioni().add(MansioneEnum.AMBULATORIO);
        servizioDao.save(servizio1);
        Shift t1 = new Shift(LocalTime.of(8, 0), Duration.ofHours(6), servizio1, MansioneEnum.REPARTO, TipologiaTurno.MATTUTINO, true);
        Shift t3 = new Shift(LocalTime.of(20, 0), Duration.ofHours(3), servizio1, MansioneEnum.REPARTO, TipologiaTurno.NOTTURNO, true);
        Shift t4 = new Shift(LocalTime.of(0, 0), Duration.ofHours(8), servizio1, MansioneEnum.REPARTO, TipologiaTurno.NOTTURNO, true);
        
        userCategoryPolicyDao.save(new ConditionPolicy(categoriaMalattia, t1, UserCategoryPolicyValue.EXCLUDE));
        userCategoryPolicyDao.save(new ConditionPolicy(categoriaFerie, t1, UserCategoryPolicyValue.EXCLUDE));
        userCategoryPolicyDao.save(new ConditionPolicy(categoriaIncinta, t3, UserCategoryPolicyValue.EXCLUDE));
        userCategoryPolicyDao.save(new ConditionPolicy(categoriaOVER62, t3, UserCategoryPolicyValue.EXCLUDE));
        userCategoryPolicyDao.save(new ConditionPolicy(categoriaMalattia, t3, UserCategoryPolicyValue.EXCLUDE));
        userCategoryPolicyDao.save(new ConditionPolicy(categoriaFerie, t3, UserCategoryPolicyValue.EXCLUDE));
        userCategoryPolicyDao.save(new ConditionPolicy(categoriaIncinta, t4, UserCategoryPolicyValue.EXCLUDE));
        userCategoryPolicyDao.save(new ConditionPolicy(categoriaOVER62, t4, UserCategoryPolicyValue.EXCLUDE));
        userCategoryPolicyDao.save(new ConditionPolicy(categoriaMalattia, t4, UserCategoryPolicyValue.EXCLUDE));
        userCategoryPolicyDao.save(new ConditionPolicy(categoriaFerie, t4, UserCategoryPolicyValue.EXCLUDE));

        turnoDao.save(t1);
        turnoDao.save(t3);
        turnoDao.save(t4);

        Shift t5 = new Shift(LocalTime.of(10, 0), Duration.ofHours(2), servizio1, MansioneEnum.AMBULATORIO, TipologiaTurno.MATTUTINO, true);
        
        turnoDao.saveAndFlush(t3);
        turnoDao.saveAndFlush(t5);
        //Crea utente
        Doctor doctor = new Doctor("Giulia","Rossi", "GLRRSS******", LocalDate.of(1999, 3, 14),"glrss@gmail.com","", RuoloEnum.SPECIALIZZANDO, AttoreEnum.UTENTE  );
        utenteDao.saveAndFlush(doctor);

        //Aggiungi assegnazione turno
        AssegnazioneTurno turnoNotte = new AssegnazioneTurno(LocalDate.of(2023,1, 10),t3,new HashSet<>(),new HashSet<>(Collections.singletonList(doctor)));
        AssegnazioneTurno turnoMattina = new AssegnazioneTurno(LocalDate.of(2023,1, 11),t5,new HashSet<>(),new HashSet<>());
        assegnazioneTurnoDao.save(turnoNotte);
        assegnazioneTurnoDao.save(turnoMattina);

        Schedule scheduleTest = new Schedule();
        scheduleTest.setAssegnazioniTurno(new ArrayList<>(Collections.singletonList(turnoNotte)));

        UserScheduleState usstate = new UserScheduleState(doctor, scheduleTest);

        Vincolo vincoloTurniContigui = new VincoloTipologieTurniContigue(
            20,
            ChronoUnit.HOURS,
            TipologiaTurno.NOTTURNO,
            new HashSet<>(Arrays.asList(TipologiaTurno.values()))); // nessun turno può essere allocato a questa persona durante il suo smonto notte
        vincoloTurniContigui.verificaVincolo(new ContestoVincolo(usstate,turnoMattina));
    }
}
