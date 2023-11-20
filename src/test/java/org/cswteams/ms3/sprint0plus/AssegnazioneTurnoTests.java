package org.cswteams.ms3.sprint0plus;

import org.cswteams.ms3.control.utils.MappaServizio;
import org.cswteams.ms3.control.utils.MappaUtenti;
import org.cswteams.ms3.dao.AssegnazioneTurnoDao;
import org.cswteams.ms3.dto.ModificaAssegnazioneTurnoDTO;
import org.cswteams.ms3.dto.RegistraAssegnazioneTurnoDTO;
import org.cswteams.ms3.dto.ServizioDTO;
import org.cswteams.ms3.entity.AssegnazioneTurno;
import org.cswteams.ms3.entity.Schedule;
import org.cswteams.ms3.entity.Utente;
import org.cswteams.ms3.enums.MansioneEnum;
import org.cswteams.ms3.enums.TipologiaTurno;
import org.cswteams.ms3.exception.AssegnazioneTurnoException;
import org.cswteams.ms3.sprint0plus.utils.ControllerSchedulerTests;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.*;

import static org.cswteams.ms3.sprint0plus.utils.ControllerSchedulerTests.TestDatesEnum.TODAY;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Profile("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class AssegnazioneTurnoTests extends ControllerSchedulerTests {
@Autowired
AssegnazioneTurnoDao assegnazioneTurnoDao;


    /**
     * Oss.: Internal vs external (via REST endpoint)
     */
    @Test
    public void addAssegnazioneTurnoExternalTest() throws AssegnazioneTurnoException {
        //TODO mock?
        //TODO catch exc che è in segnatura
        RegistraAssegnazioneTurnoDTO at = new RegistraAssegnazioneTurnoDTO();

        //use already inserted data
        ServizioDTO servizioDTO = MappaServizio.servizioEntitytoDTO(servizioDao.findByNome("cardiologia"));

        Utente uGuardia = utenteDao.findByEmailAndPassword("glrss@gmail.com", "");
        List<Utente> uGuardiaList= new ArrayList<>();
        uGuardiaList.add(uGuardia);

        Utente uReperibili = utenteDao.findByEmailAndPassword("salvatimartina97@gmail.com", "");
        List<Utente> uReperibiliList= new ArrayList<>();
        uReperibiliList.add(uReperibili);

        at.setServizio(servizioDTO);
        at.setTipologiaTurno(TipologiaTurno.NOTTURNO);
        at.setMansione(MansioneEnum.REPARTO);
        at.setAnno(testDates.get(TODAY).getYear());
        at.setMese(testDates.get(TODAY).getMonthValue());
        at.setGiorno(testDates.get(TODAY).getDayOfMonth());
        at.setUtentiDiGuardia(MappaUtenti.utentiEntitytoDTO(uGuardiaList));
        at.setUtentiReperibili(MappaUtenti.utentiEntitytoDTO(uReperibiliList));

        Schedule s=this.instance.createSchedule(testDates.get(TODAY).minusDays(2), testDates.get(TODAY).plusDays(3));


        Schedule schedule = this.instance.aggiungiAssegnazioneTurno(at, true);
        Assert.assertNotNull(schedule);
        Schedule schedule2 = this.instance.aggiungiAssegnazioneTurno(at, false);
        Assert.assertNotNull(schedule2);

    }




















    @Test
    public void updateAssegnazioneTurnoTest() throws AssegnazioneTurnoException {    //TODO mock?
        //TODO catch exc che è in segnatura
        RegistraAssegnazioneTurnoDTO at = new RegistraAssegnazioneTurnoDTO();

        //use already inserted data
        ServizioDTO servizioDTO = MappaServizio.servizioEntitytoDTO(servizioDao.findByNome("cardiologia"));

        Utente uGuardia = utenteDao.findByEmailAndPassword("glrss@gmail.com", "");
        List<Utente> uGuardiaList= new ArrayList<>();
        uGuardiaList.add(uGuardia);

        Utente uReperibili = utenteDao.findByEmailAndPassword("salvatimartina97@gmail.com", "");
        List<Utente> uReperibiliList= new ArrayList<>();
        uReperibiliList.add(uReperibili);

        at.setServizio(servizioDTO);
        at.setTipologiaTurno(TipologiaTurno.NOTTURNO);
        at.setMansione(MansioneEnum.REPARTO);
        at.setAnno(testDates.get(TODAY).getYear());
        at.setMese(testDates.get(TODAY).getMonthValue());
        at.setGiorno(testDates.get(TODAY).getDayOfMonth());
        at.setUtentiDiGuardia(MappaUtenti.utentiEntitytoDTO(uGuardiaList));
        at.setUtentiReperibili(MappaUtenti.utentiEntitytoDTO(uReperibiliList));

        Schedule s=this.instance.createSchedule(testDates.get(TODAY).minusDays(2), testDates.get(TODAY).plusDays(3));


        Schedule schedule = this.instance.aggiungiAssegnazioneTurno(at, true);

        //-----------------------------
        Long id= schedule.getAssegnazioniTurno().get(0).getId();
        long[] guardiaIdList={uGuardia.getId()};
        System.out.println(guardiaIdList[0]);

        long[] reperibiliIdList={uReperibili.getId()};

        ModificaAssegnazioneTurnoDTO mat=new ModificaAssegnazioneTurnoDTO(
                id,
                reperibiliIdList,   // (inverted)
                guardiaIdList,      // (inverted)
                uGuardia.getId()
        );

        Schedule ss=this.instance.modificaAssegnazioneTurno(mat);
        Assert.assertNotNull(ss);
        Optional<AssegnazioneTurno> atRetrieved=assegnazioneTurnoDao.findById(id);
        Assert.assertEquals((AssegnazioneTurno)at.getUtentiDiGuardia(),uReperibiliList);
        Assert.assertEquals((AssegnazioneTurno)at.getUtentiReperibili(),uGuardiaList);
    }


    /**
     * Oss.: Internal vs external (via REST endpoint)
     */
    @Test
    public void removeAssegnazioneTurnoExternalTest() throws AssegnazioneTurnoException {
        RegistraAssegnazioneTurnoDTO at = new RegistraAssegnazioneTurnoDTO();

        //use already inserted data
        ServizioDTO servizioDTO = MappaServizio.servizioEntitytoDTO(servizioDao.findByNome("cardiologia"));

        Utente uGuardia = utenteDao.findByEmailAndPassword("glrss@gmail.com", "");
        List<Utente> uGuardiaList= new ArrayList<>();
        uGuardiaList.add(uGuardia);

        Utente uReperibili = utenteDao.findByEmailAndPassword("salvatimartina97@gmail.com", "");
        List<Utente> uReperibiliList= new ArrayList<>();
        uReperibiliList.add(uReperibili);

        at.setServizio(servizioDTO);
        at.setTipologiaTurno(TipologiaTurno.NOTTURNO);
        at.setMansione(MansioneEnum.REPARTO);
        at.setAnno(testDates.get(TODAY).getYear());
        at.setMese(testDates.get(TODAY).getMonthValue());
        at.setGiorno(testDates.get(TODAY).getDayOfMonth());
        at.setUtentiDiGuardia(MappaUtenti.utentiEntitytoDTO(uGuardiaList));
        at.setUtentiReperibili(MappaUtenti.utentiEntitytoDTO(uReperibiliList));

        Schedule s=this.instance.createSchedule(testDates.get(TODAY).minusDays(2), testDates.get(TODAY).plusDays(3));


        Schedule schedule = this.instance.aggiungiAssegnazioneTurno(at, true);

        //-----------------------------
        Long id= schedule.getAssegnazioniTurno().get(0).getId();


        this.instance.rimuoviAssegnazioneTurno(id);
        //TODO con assegnazione turni vuota (isEmpty)
    }

}
