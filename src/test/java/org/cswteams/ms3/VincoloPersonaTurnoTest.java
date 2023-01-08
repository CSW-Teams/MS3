package org.cswteams.ms3;

import org.cswteams.ms3.control.vincoli.ContestoVincolo;
import org.cswteams.ms3.control.vincoli.VincoloPersonaTurno;
import org.cswteams.ms3.control.vincoli.ViolatedConstraintException;
import org.cswteams.ms3.dao.*;
import org.cswteams.ms3.entity.*;
import org.cswteams.ms3.enums.CategoriaUtentiEnum;
import org.cswteams.ms3.enums.RuoloEnum;
import org.cswteams.ms3.enums.TipologiaTurno;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.HashSet;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD) // The spring application context will be considered "dirty" before each test method, and will be rebuilt. It means that
@Transactional
public class VincoloPersonaTurnoTest {

    @Autowired
    private UtenteDao utenteDao;

    @Autowired
    private AssegnazioneTurnoDao assegnazioneTurnoDao;

    @Autowired
    private TurnoDao turnoDao;

    @Autowired
    private ServizioDao servizioDao;

    @Autowired
    private CategoriaUtenteDao categoriaUtenteDao;

    private VincoloPersonaTurno vincoloPersonaTurno = new VincoloPersonaTurno();

    @Test(expected=ViolatedConstraintException.class)
    /**Test che verifica che una persona incinta non può essere aggiunta ad un turno notturno -
     *  violazione del vincolo categoria. */
    public void pregnancyTEST() throws ViolatedConstraintException {
        //Crea turni e servizio
        Servizio servizio1 = new Servizio("reparto");
        servizioDao.save(servizio1);
        Turno t1 = new Turno(LocalTime.of(8, 0), LocalTime.of(14, 0), servizio1, TipologiaTurno.MATTUTINO, new HashSet<>(Arrays.asList(CategoriaUtentiEnum.IN_MALATTIA, CategoriaUtentiEnum.IN_FERIE)));
        Turno t3 = new Turno(LocalTime.of(20, 0), LocalTime.of(23, 0), servizio1, TipologiaTurno.NOTTURNO, new HashSet<>(Arrays.asList(CategoriaUtentiEnum.DONNA_INCINTA, CategoriaUtentiEnum.OVER_62, CategoriaUtentiEnum.IN_MALATTIA, CategoriaUtentiEnum.IN_FERIE)));
        Turno t4 = new Turno(LocalTime.of(0, 0), LocalTime.of(8, 0), servizio1, TipologiaTurno.NOTTURNO, new HashSet<>(Arrays.asList(CategoriaUtentiEnum.DONNA_INCINTA, CategoriaUtentiEnum.OVER_62, CategoriaUtentiEnum.IN_MALATTIA, CategoriaUtentiEnum.IN_FERIE)));
        turnoDao.save(t1);
        turnoDao.save(t3);
        turnoDao.save(t4);
        //Aggiungi assegnazione turno
        AssegnazioneTurno turnoNotturno = new AssegnazioneTurno(LocalDate.of(2023,1, 10),t3,null,null);
        assegnazioneTurnoDao.save(turnoNotturno);
        //Aggiungi categoria all'utente
        CategoriaUtente incinta = new CategoriaUtente(CategoriaUtentiEnum.DONNA_INCINTA, LocalDate.of(2023, 1, 4), LocalDate.of(2023, 10, 4));
        categoriaUtenteDao.saveAndFlush(incinta);
        //Crea utente - PERSONA INCINTA
        Utente pregUser = new Utente("Giulia","Rossi", "GLRRSS******", LocalDate.of(1999, 3, 14),"glrss@gmail.com", RuoloEnum.SPECIALIZZANDO );
        pregUser.getCategorie().add(incinta);
        utenteDao.saveAndFlush(pregUser);
        //La persona incinta non può essere aggiunta ai turni notturni, l'eccezione deve essere sollevata
        vincoloPersonaTurno.verificaVincolo(new ContestoVincolo(pregUser,turnoNotturno));
    }

    @Test(expected=ViolatedConstraintException.class)
    /**Test che verifica che una persona over62 non può essere aggiunta ad un turno notturno
     *  violazione del vincolo categoria. */
    public void over62TEST() throws ViolatedConstraintException {
        //Crea turni e servizio
        Servizio servizio1 = new Servizio("reparto");
        servizioDao.save(servizio1);
        Turno t1 = new Turno(LocalTime.of(8, 0), LocalTime.of(14, 0), servizio1, TipologiaTurno.MATTUTINO, new HashSet<>(Arrays.asList(CategoriaUtentiEnum.IN_MALATTIA, CategoriaUtentiEnum.IN_FERIE)));
        Turno t3 = new Turno(LocalTime.of(20, 0), LocalTime.of(23, 0), servizio1, TipologiaTurno.NOTTURNO, new HashSet<>(Arrays.asList(CategoriaUtentiEnum.DONNA_INCINTA, CategoriaUtentiEnum.OVER_62, CategoriaUtentiEnum.IN_MALATTIA, CategoriaUtentiEnum.IN_FERIE)));
        Turno t4 = new Turno(LocalTime.of(0, 0), LocalTime.of(8, 0), servizio1, TipologiaTurno.NOTTURNO, new HashSet<>(Arrays.asList(CategoriaUtentiEnum.DONNA_INCINTA, CategoriaUtentiEnum.OVER_62, CategoriaUtentiEnum.IN_MALATTIA, CategoriaUtentiEnum.IN_FERIE)));
        turnoDao.save(t1);
        turnoDao.save(t3);
        turnoDao.save(t4);
        //Aggiungi assegnazione turno
        AssegnazioneTurno turnoNotturno = new AssegnazioneTurno(LocalDate.of(2023,1, 10),t3,null,null);
        assegnazioneTurnoDao.save(turnoNotturno);
        //Crea categoria
        CategoriaUtente over62 = new CategoriaUtente(CategoriaUtentiEnum.OVER_62,LocalDate.of(2023, 1, 4), LocalDate.of(2100, 10, 4));
        categoriaUtenteDao.saveAndFlush(over62);
        //Crea utente - PERSONA OVER 62
        Utente over62user = new Utente("Stefano","Rossi", "STFRSS******", LocalDate.of(1953, 3, 14),"stfrss@gmail.com", RuoloEnum.STRUTTURATO );
        over62user.getCategorie().add(over62);
        utenteDao.saveAndFlush(over62user);
        //Verifica il vincolo
        //La persona over62 non può essere aggiunta ai turni  notturni, l'eccezioen deve essere sollevata
        vincoloPersonaTurno.verificaVincolo(new ContestoVincolo(over62user,turnoNotturno));
    }

    @Test(expected=ViolatedConstraintException.class)
    /**Test che verifica che una persona in malattia non può essere aggiunta ad un turno notturno
     *  violazione del vincolo categoria. */
    public void InMalattiaTEST() throws ViolatedConstraintException {
        //Crea turni e servizio
        Servizio servizio1 = new Servizio("reparto");
        servizioDao.save(servizio1);
        Turno t1 = new Turno(LocalTime.of(8, 0), LocalTime.of(14, 0), servizio1, TipologiaTurno.MATTUTINO, new HashSet<>(Arrays.asList(CategoriaUtentiEnum.IN_MALATTIA, CategoriaUtentiEnum.IN_FERIE)));
        Turno t3 = new Turno(LocalTime.of(20, 0), LocalTime.of(23, 0), servizio1, TipologiaTurno.NOTTURNO, new HashSet<>(Arrays.asList(CategoriaUtentiEnum.DONNA_INCINTA, CategoriaUtentiEnum.OVER_62, CategoriaUtentiEnum.IN_MALATTIA, CategoriaUtentiEnum.IN_FERIE)));
        turnoDao.save(t1);
        turnoDao.save(t3);
        //Aggiungi assegnazione turno
        AssegnazioneTurno turnoMattutinoinmalattia = new AssegnazioneTurno(LocalDate.of(2023,1, 4),t1,null,null);
        assegnazioneTurnoDao.save(turnoMattutinoinmalattia);
        //Crea categoria IN MALATTIA
        CategoriaUtente inmalattia = new CategoriaUtente(CategoriaUtentiEnum.IN_MALATTIA,LocalDate.of(2023, 1, 4), LocalDate.of(2100, 10, 4));
        categoriaUtenteDao.saveAndFlush(inmalattia);
        //Crea utente - PERSONA IN MALATTIA
        Utente inmalattiauser = new Utente("Stefano","Rossi", "STFRSS******", LocalDate.of(1953, 3, 14),"stfrss@gmail.com", RuoloEnum.STRUTTURATO );
        inmalattiauser.getCategorie().add(inmalattia);
        utenteDao.saveAndFlush(inmalattiauser);
        //Verifica il vincolo
        //La persona in malattia non può essere aggiunta ai turni durante la malattia, quindi l'eccezione deve essere sollevata
        vincoloPersonaTurno.verificaVincolo(new ContestoVincolo(inmalattiauser,turnoMattutinoinmalattia));
    }

    @Test(expected=ViolatedConstraintException.class)
    /**Test che verifica che una persona in ferie non può essere aggiunta ad alcun turno -
     * violazione del vincolo categoria. */
    public void InFerieTEST() throws ViolatedConstraintException {
        //Crea turni e servizio
        Servizio servizio1 = new Servizio("reparto");
        servizioDao.save(servizio1);
        Turno t1 = new Turno(LocalTime.of(8, 0), LocalTime.of(14, 0), servizio1, TipologiaTurno.MATTUTINO, new HashSet<>(Arrays.asList(CategoriaUtentiEnum.IN_MALATTIA, CategoriaUtentiEnum.IN_FERIE)));
        Turno t3 = new Turno(LocalTime.of(20, 0), LocalTime.of(23, 0), servizio1, TipologiaTurno.NOTTURNO, new HashSet<>(Arrays.asList(CategoriaUtentiEnum.DONNA_INCINTA, CategoriaUtentiEnum.OVER_62, CategoriaUtentiEnum.IN_MALATTIA, CategoriaUtentiEnum.IN_FERIE)));
        turnoDao.save(t1);
        turnoDao.save(t3);
        //Aggiungi assegnazione turno
        AssegnazioneTurno turnoMattutinoiferie = new AssegnazioneTurno(LocalDate.of(2023,1, 4),t1,null,null);
        assegnazioneTurnoDao.save(turnoMattutinoiferie);
        //Crea categoria IN MALATTIA
        CategoriaUtente inferie = new CategoriaUtente(CategoriaUtentiEnum.IN_FERIE,LocalDate.of(2023, 1, 4), LocalDate.of(2100, 10, 4));
        categoriaUtenteDao.saveAndFlush(inferie);
        //Crea utente - PERSONA IN MALATTIA
        Utente inferieuser = new Utente("Stefano","Rossi", "STFRSS******", LocalDate.of(1953, 3, 14),"stfrss@gmail.com", RuoloEnum.STRUTTURATO );
        inferieuser.getCategorie().add(inferie);
        utenteDao.saveAndFlush(inferieuser);
        //Verifica il vincolo
        //La persona in malattia non può essere aggiunta ai turni durante la malattia, quindi l'eccezione deve essere sollevata
        vincoloPersonaTurno.verificaVincolo(new ContestoVincolo(inferieuser,turnoMattutinoiferie));
    }



}
