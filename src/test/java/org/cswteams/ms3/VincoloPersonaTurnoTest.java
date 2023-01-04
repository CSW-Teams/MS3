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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.validation.constraints.AssertTrue;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.HashSet;

@SpringBootTest
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

    @Test
    public void pregnancyTest(){
        Utente u1 = new Utente("Giulia","Cantone", "SLVMTN******", LocalDate.of(1997, 3, 14),"salvatimartina97@gmail.com", RuoloEnum.SPECIALIZZANDO );
        Utente u2 = new Utente("Domenico","Verde", "DMNCVRD******", LocalDate.of(1997, 5, 23),"domenicoverde@gmail.com", RuoloEnum.SPECIALIZZANDO);
        Utente u3 = new Utente("Daniele","Colavecchi", "DNLCLV******", LocalDate.of(1982, 7, 6),"danielecolavecchi@gmail.com", RuoloEnum.STRUTTURATO);

        u1 = utenteDao.save(u1);
        u2 = utenteDao.save(u2);
        u3 = utenteDao.save(u3);

        CategoriaUtente incinta = new CategoriaUtente(CategoriaUtentiEnum.DONNA_INCINTA,u1.getId(),LocalDate.of(2023, 1, 4), LocalDate.of(2023, 10, 4));
        categoriaUtenteDao.save(incinta);

        Servizio servizio1 = new Servizio("reparto");
        servizioDao.save(servizio1);

        Servizio servizio2 = new Servizio("ambulatorio");
        servizioDao.save(servizio2);

        Turno t2 = new Turno(LocalTime.of(14, 0), LocalTime.of(20, 0), servizio1, TipologiaTurno.POMERIDIANO, new HashSet<>(Arrays.asList(CategoriaUtentiEnum.IN_MALATTIA, CategoriaUtentiEnum.IN_FERIE)));
        Turno t3 = new Turno(LocalTime.of(20, 0), LocalTime.of(23, 0), servizio1, TipologiaTurno.NOTTURNO, new HashSet<>(Arrays.asList(CategoriaUtentiEnum.DONNA_INCINTA, CategoriaUtentiEnum.OVER_62, CategoriaUtentiEnum.IN_MALATTIA, CategoriaUtentiEnum.IN_FERIE)));
        Turno t4 = new Turno(LocalTime.of(0, 0), LocalTime.of(8, 0), servizio1, TipologiaTurno.NOTTURNO, new HashSet<>(Arrays.asList(CategoriaUtentiEnum.DONNA_INCINTA, CategoriaUtentiEnum.OVER_62, CategoriaUtentiEnum.IN_MALATTIA, CategoriaUtentiEnum.IN_FERIE)));
        Turno t5 = new Turno(LocalTime.of(10, 0), LocalTime.of(12, 0), servizio2, TipologiaTurno.MATTUTINO, new HashSet<>(Arrays.asList(CategoriaUtentiEnum.IN_MALATTIA, CategoriaUtentiEnum.IN_FERIE)));

        turnoDao.save(t2);
        turnoDao.save(t3);
        turnoDao.save(t4);
        turnoDao.save(t5);

        Vincolo v1 = new VincoloPersonaTurno();
        AssegnazioneTurno a1 = new AssegnazioneTurno(LocalDate.of(2023,1, 10),t3,null,null);

        Assert.assertFalse(v1.verificaVincolo(new ContestoVincolo(u1,a1)));
        Assert.assertTrue(v1.verificaVincolo(new ContestoVincolo(u2,a1)));
    }


}
