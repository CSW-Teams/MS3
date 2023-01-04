package org.cswteams.ms3;

import org.cswteams.ms3.control.vincoli.ContestoVincolo;
import org.cswteams.ms3.control.vincoli.Vincolo;
import org.cswteams.ms3.control.vincoli.VincoloPersonaTurno;
import org.cswteams.ms3.dao.*;
import org.cswteams.ms3.entity.*;
import org.cswteams.ms3.enums.CategoriaUtentiEnum;
import org.cswteams.ms3.enums.RuoloEnum;
import org.cswteams.ms3.enums.TipologiaTurno;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.validation.constraints.AssertTrue;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.HashSet;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
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

    @Autowired
    private VincoloPersonaTurno vincoloPersonaTurno;

    @Test
    public void pregnancyTEST(){
        //Crea turni e servizio
        Servizio servizio1 = new Servizio("reparto");
        servizioDao.save(servizio1);
        Turno t1 = new Turno(LocalTime.of(8, 0), LocalTime.of(14, 0), servizio1, TipologiaTurno.MATTUTINO, new HashSet<>(Arrays.asList(CategoriaUtentiEnum.IN_MALATTIA, CategoriaUtentiEnum.IN_FERIE)));
        Turno t3 = new Turno(LocalTime.of(20, 0), LocalTime.of(23, 0), servizio1, TipologiaTurno.NOTTURNO, new HashSet<>(Arrays.asList(CategoriaUtentiEnum.DONNA_INCINTA, CategoriaUtentiEnum.OVER_62, CategoriaUtentiEnum.IN_MALATTIA, CategoriaUtentiEnum.IN_FERIE)));
        Turno t4 = new Turno(LocalTime.of(0, 0), LocalTime.of(8, 0), servizio1, TipologiaTurno.NOTTURNO, new HashSet<>(Arrays.asList(CategoriaUtentiEnum.DONNA_INCINTA, CategoriaUtentiEnum.OVER_62, CategoriaUtentiEnum.IN_MALATTIA, CategoriaUtentiEnum.IN_FERIE)));
        turnoDao.save(t1);
        turnoDao.save(t3);
        turnoDao.save(t4);
        //Aggiungi vincoli ai turni
        AssegnazioneTurno turnoMattutino = new AssegnazioneTurno(LocalDate.of(2023,1, 10),t1,null,null);
        AssegnazioneTurno turnoNotturno = new AssegnazioneTurno(LocalDate.of(2023,1, 10),t3,null,null);
        assegnazioneTurnoDao.save(turnoNotturno);
        assegnazioneTurnoDao.save(turnoMattutino);
        //Crea utente - PERSONA INCINTA
        Utente pregUser = new Utente("Giulia","Rossi", "GLRRSS******", LocalDate.of(1954, 3, 14),"glrss@gmail.com", RuoloEnum.SPECIALIZZANDO );
        utenteDao.save(pregUser);
        //Aggiungi categoria all'utente
        CategoriaUtente incinta = new CategoriaUtente(CategoriaUtentiEnum.DONNA_INCINTA,pregUser.getId(),LocalDate.of(2023, 1, 4), LocalDate.of(2023, 10, 4));
        categoriaUtenteDao.save(incinta);
        //Verifica il vincolo
        // la persona incinta può essere aggiunta ai turni mattutini ma non notturni
        Assert.assertFalse(vincoloPersonaTurno.verificaVincolo(new ContestoVincolo(pregUser,turnoNotturno)));
        Assert.assertTrue(vincoloPersonaTurno.verificaVincolo(new ContestoVincolo(pregUser,turnoMattutino)));
    }
    @Test
    public void over62TEST(){
        //Crea turni e servizio
        Servizio servizio1 = new Servizio("reparto");
        servizioDao.save(servizio1);
        Turno t1 = new Turno(LocalTime.of(8, 0), LocalTime.of(14, 0), servizio1, TipologiaTurno.MATTUTINO, new HashSet<>(Arrays.asList(CategoriaUtentiEnum.IN_MALATTIA, CategoriaUtentiEnum.IN_FERIE)));
        Turno t3 = new Turno(LocalTime.of(20, 0), LocalTime.of(23, 0), servizio1, TipologiaTurno.NOTTURNO, new HashSet<>(Arrays.asList(CategoriaUtentiEnum.DONNA_INCINTA, CategoriaUtentiEnum.OVER_62, CategoriaUtentiEnum.IN_MALATTIA, CategoriaUtentiEnum.IN_FERIE)));
        Turno t4 = new Turno(LocalTime.of(0, 0), LocalTime.of(8, 0), servizio1, TipologiaTurno.NOTTURNO, new HashSet<>(Arrays.asList(CategoriaUtentiEnum.DONNA_INCINTA, CategoriaUtentiEnum.OVER_62, CategoriaUtentiEnum.IN_MALATTIA, CategoriaUtentiEnum.IN_FERIE)));
        turnoDao.save(t1);
        turnoDao.save(t3);
        turnoDao.save(t4);
        //Aggiungi vincoli ai turni
        AssegnazioneTurno turnoMattutino = new AssegnazioneTurno(LocalDate.of(2023,1, 10),t1,null,null);
        AssegnazioneTurno turnoNotturno = new AssegnazioneTurno(LocalDate.of(2023,1, 10),t3,null,null);
        assegnazioneTurnoDao.save(turnoNotturno);
        assegnazioneTurnoDao.save(turnoMattutino);
        //Crea utente - PERSONA OVER 62
        Utente over62user = new Utente("Stefano","Rossi", "STFRSS******", LocalDate.of(1953, 3, 14),"stfrss@gmail.com", RuoloEnum.STRUTTURATO );
        utenteDao.save(over62user);
        //Aggiungi categoria all'utente
        CategoriaUtente over62 = new CategoriaUtente(CategoriaUtentiEnum.OVER_62,over62user.getId(),LocalDate.of(2023, 1, 4), LocalDate.of(2100, 10, 4));
        categoriaUtenteDao.save(over62);
        //Verifica il vincolo
        // la persona over62 può essere aggiunta ai turni mattutini ma non notturni
        Assert.assertFalse(vincoloPersonaTurno.verificaVincolo(new ContestoVincolo(over62user,turnoNotturno)));
        Assert.assertTrue(vincoloPersonaTurno.verificaVincolo(new ContestoVincolo(over62user,turnoMattutino)));
    }

    @Test
    public void InMalattiaTEST(){
        //Crea turni e servizio
        Servizio servizio1 = new Servizio("reparto");
        servizioDao.save(servizio1);
        Turno t1 = new Turno(LocalTime.of(8, 0), LocalTime.of(14, 0), servizio1, TipologiaTurno.MATTUTINO, new HashSet<>(Arrays.asList(CategoriaUtentiEnum.IN_MALATTIA, CategoriaUtentiEnum.IN_FERIE)));
        Turno t3 = new Turno(LocalTime.of(20, 0), LocalTime.of(23, 0), servizio1, TipologiaTurno.NOTTURNO, new HashSet<>(Arrays.asList(CategoriaUtentiEnum.DONNA_INCINTA, CategoriaUtentiEnum.OVER_62, CategoriaUtentiEnum.IN_MALATTIA, CategoriaUtentiEnum.IN_FERIE)));
        turnoDao.save(t1);
        turnoDao.save(t3);
        //Aggiungi vincoli ai turni
        AssegnazioneTurno turnoMattutinoinmalattia = new AssegnazioneTurno(LocalDate.of(2023,1, 4),t1,null,null);
        AssegnazioneTurno turnoNotturnoinmalattia = new AssegnazioneTurno(LocalDate.of(2023,1, 4),t3,null,null);
        AssegnazioneTurno turnoNotturnodopomalattia = new AssegnazioneTurno(LocalDate.of(2023,1, 10),t3,null,null);
        assegnazioneTurnoDao.save(turnoMattutinoinmalattia);
        assegnazioneTurnoDao.save(turnoNotturnoinmalattia);
        assegnazioneTurnoDao.save(turnoNotturnodopomalattia);
        //Crea utente - PERSONA IN MALATTIA
        Utente inmalattiauser = new Utente("Stefano","Rossi", "STFRSS******", LocalDate.of(1983, 3, 14),"stfrss@gmail.com", RuoloEnum.STRUTTURATO );
        utenteDao.save(inmalattiauser);
        //Aggiungi categoria all'utente
        //utente in malattia per 5 giorni
        CategoriaUtente malattia = new CategoriaUtente(CategoriaUtentiEnum.IN_MALATTIA,inmalattiauser.getId(),LocalDate.of(2023, 1, 4), LocalDate.of(2023, 1, 9));
        categoriaUtenteDao.save(malattia);
        //Verifica il vincolo
        // la persona over62 può essere aggiunta ad alcun turno durante la malattia
        Assert.assertFalse(vincoloPersonaTurno.verificaVincolo(new ContestoVincolo(inmalattiauser,turnoNotturnoinmalattia)));
        Assert.assertFalse(vincoloPersonaTurno.verificaVincolo(new ContestoVincolo(inmalattiauser,turnoMattutinoinmalattia)));
        // ma può essere aggiunto ai turni dopo la malattia
        Assert.assertTrue(vincoloPersonaTurno.verificaVincolo(new ContestoVincolo(inmalattiauser,turnoNotturnodopomalattia)));
    }


}
