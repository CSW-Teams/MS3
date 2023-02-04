package org.cswteams.ms3;

import org.cswteams.ms3.dao.*;
import org.cswteams.ms3.entity.vincoli.ContestoVincolo;
import org.cswteams.ms3.entity.vincoli.Vincolo;
import org.cswteams.ms3.entity.vincoli.VincoloMaxPeriodoConsecutivo;
import org.cswteams.ms3.dao.AssegnazioneTurnoDao;
import org.cswteams.ms3.dao.ServizioDao;
import org.cswteams.ms3.dao.TurnoDao;
import org.cswteams.ms3.dao.UserCategoryPolicyDao;
import org.cswteams.ms3.dao.UtenteDao;
import org.cswteams.ms3.entity.*;
import org.cswteams.ms3.enums.MansioneEnum;
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
public class VincoloMaxPeriodoConsecutivoTest {

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

    @Test(expected= ViolatedConstraintException.class)
    /**Test che verifica che un utente non può effettuare più di un tot ore consecutive */
    public void oreConsecutiveTEST() throws ViolatedConstraintException, TurnoException {
        //CREA LE CATEGORIE DI TIPO STATO (ESCLUSIVE PER I TURNI)
        Categoria categoriaOVER62 = new Categoria("OVER_62", 0);
        Categoria categoriaIncinta = new Categoria("INCINTA", 0);
        Categoria categoriaFerie = new Categoria("IN_FERIE", 0);
        Categoria categoriaMalattia = new Categoria("IN_MALATTIA", 0);
        //Crea turni e servizio
        Servizio servizio1 = new Servizio("cardiologia");
        servizio1.getMansioni().add(MansioneEnum.REPARTO);
        servizioDao.save(servizio1);
        Turno t1 = new Turno(LocalTime.of(8, 0), LocalTime.of(14, 0), servizio1, MansioneEnum.REPARTO, TipologiaTurno.MATTUTINO,Arrays.asList(new RuoloNumero(RuoloEnum.SPECIALIZZANDO, 1), new RuoloNumero(RuoloEnum.STRUTTURATO, 1)));
        Turno t2 = new Turno(LocalTime.of(14, 0), LocalTime.of(20, 0), servizio1, MansioneEnum.REPARTO,  TipologiaTurno.POMERIDIANO, Arrays.asList(new RuoloNumero(RuoloEnum.SPECIALIZZANDO, 1), new RuoloNumero(RuoloEnum.STRUTTURATO, 1)));
        Turno t3 = new Turno(LocalTime.of(20, 0), LocalTime.of(8, 0), servizio1, MansioneEnum.REPARTO,  TipologiaTurno.NOTTURNO, Arrays.asList(new RuoloNumero(RuoloEnum.SPECIALIZZANDO, 1), new RuoloNumero(RuoloEnum.STRUTTURATO, 1)));
        //Turno t4 = new Turno(LocalTime.of(0, 0), LocalTime.of(8, 0), servizio1, TipologiaTurno.NOTTURNO, new HashSet<>(Arrays.asList(CategoriaUtentiEnum.DONNA_INCINTA, CategoriaUtentiEnum.OVER_62, CategoriaUtentiEnum.IN_MALATTIA, CategoriaUtentiEnum.IN_FERIE)));

        userCategoryPolicyDao.save(new UserCategoryPolicy(categoriaOVER62, t1, UserCategoryPolicyValue.EXCLUDE));
        userCategoryPolicyDao.save(new UserCategoryPolicy(categoriaIncinta, t1, UserCategoryPolicyValue.EXCLUDE));
        userCategoryPolicyDao.save(new UserCategoryPolicy(categoriaMalattia, t2, UserCategoryPolicyValue.EXCLUDE));
        userCategoryPolicyDao.save(new UserCategoryPolicy(categoriaFerie, t2, UserCategoryPolicyValue.EXCLUDE));
        userCategoryPolicyDao.save(new UserCategoryPolicy(categoriaIncinta, t3, UserCategoryPolicyValue.EXCLUDE));
        userCategoryPolicyDao.save(new UserCategoryPolicy(categoriaOVER62, t3, UserCategoryPolicyValue.EXCLUDE));
        userCategoryPolicyDao.save(new UserCategoryPolicy(categoriaMalattia, t3, UserCategoryPolicyValue.EXCLUDE));
        userCategoryPolicyDao.save(new UserCategoryPolicy(categoriaFerie, t3, UserCategoryPolicyValue.EXCLUDE));

        turnoDao.save(t1);
        turnoDao.save(t2);
        turnoDao.save(t3);
        //turnoDao.save(t4);
        //Crea utente
        Utente utente = new Utente("Giulia","Rossi", "GLRRSS******", LocalDate.of(1999, 3, 14),"glrss@gmail.com", RuoloEnum.SPECIALIZZANDO );
        utenteDao.save(utente);

        //Aggiungi assegnazione turno
        AssegnazioneTurno turnoPomeriggio = new AssegnazioneTurno(LocalDate.of(2023,1, 10),t2,new HashSet<>(),new HashSet<>(Collections.singletonList(utente)));
        AssegnazioneTurno turnoNotturno = new AssegnazioneTurno(LocalDate.of(2023,1, 10),t3,new HashSet<>(),new HashSet<>(Collections.singletonList(utente)));
        assegnazioneTurnoDao.save(turnoPomeriggio);
        assegnazioneTurnoDao.save(turnoNotturno);

        Schedule scheduleTest = new Schedule();
        scheduleTest.setAssegnazioniTurno(new ArrayList<>(Collections.singletonList(turnoPomeriggio)));

        UserScheduleState pregUserState = new UserScheduleState(utente, scheduleTest);

        Vincolo vincoloMaxOreConsecutive = new VincoloMaxPeriodoConsecutivo(12*60,new ArrayList<>());
        //La persona incinta non può essere aggiunta ai turni notturni, l'eccezione deve essere sollevata
        vincoloMaxOreConsecutive.verificaVincolo(new ContestoVincolo(pregUserState,turnoNotturno));
    }


    @Test(expected= ViolatedConstraintException.class)
    /**Test che verifica che un utente non può effettuare più di un tot ore consecutive */
    public void oreConsecutiveConCategoriaTEST() throws ViolatedConstraintException, TurnoException{
        Servizio servizio1 = new Servizio("cardiologia");
        servizio1.getMansioni().add(MansioneEnum.REPARTO);
        servizioDao.save(servizio1);
        Turno t1 = new Turno(LocalTime.of(8, 0), LocalTime.of(14, 0), servizio1, MansioneEnum.REPARTO,  TipologiaTurno.MATTUTINO,Arrays.asList(new RuoloNumero(RuoloEnum.SPECIALIZZANDO, 1), new RuoloNumero(RuoloEnum.STRUTTURATO, 1)));
        Turno t2 = new Turno(LocalTime.of(14, 0), LocalTime.of(20, 0), servizio1, MansioneEnum.REPARTO,  TipologiaTurno.POMERIDIANO,Arrays.asList(new RuoloNumero(RuoloEnum.SPECIALIZZANDO, 1), new RuoloNumero(RuoloEnum.STRUTTURATO, 1)));
        Turno t3 = new Turno(LocalTime.of(20, 0), LocalTime.of(8, 0), servizio1, MansioneEnum.REPARTO,  TipologiaTurno.NOTTURNO,Arrays.asList(new RuoloNumero(RuoloEnum.SPECIALIZZANDO, 1), new RuoloNumero(RuoloEnum.STRUTTURATO, 1)));
        turnoDao.save(t1);
        turnoDao.save(t2);
        turnoDao.save(t3);

        Categoria categoriaOVER62 = new Categoria("OVER_62", 0);
        //turnoDao.save(t4);
        CategoriaUtente over62 = new CategoriaUtente(categoriaOVER62,LocalDate.of(2023, 1, 4), LocalDate.of(2100, 10, 4));
        //Crea utente
        Utente over62user = new Utente("Stefano","Rossi", "STFRSS******", LocalDate.of(1953, 3, 14),"stfrss@gmail.com", RuoloEnum.STRUTTURATO );
        over62user.getStato().add(over62);

        //Aggiungi assegnazione turno
        AssegnazioneTurno turnoMattina = new AssegnazioneTurno(LocalDate.of(2023,1, 10),t1,new HashSet<>(),new HashSet<>(Collections.singletonList(over62user)));
        AssegnazioneTurno turnoPomeriggio = new AssegnazioneTurno(LocalDate.of(2023,1, 10),t2,new HashSet<>(),new HashSet<>(Collections.singletonList(over62user)));
        AssegnazioneTurno turnoNotturno = new AssegnazioneTurno(LocalDate.of(2023,1, 10),t3,new HashSet<>(),new HashSet<>(Collections.singletonList(over62user)));

        Schedule scheduleTest = new Schedule();
        scheduleTest.setAssegnazioniTurno(new ArrayList<>(Arrays.asList(turnoMattina,turnoNotturno)));

        UserScheduleState pregUserState = new UserScheduleState(over62user, scheduleTest);

        Vincolo vincoloMaxOreConsecutive = new VincoloMaxPeriodoConsecutivo(9*60, Collections.singletonList(categoriaOVER62));

        //La persona incinta non può essere aggiunta ai turni notturni, l'eccezione deve essere sollevata
        vincoloMaxOreConsecutive.verificaVincolo(new ContestoVincolo(pregUserState,turnoPomeriggio));
    }



}
