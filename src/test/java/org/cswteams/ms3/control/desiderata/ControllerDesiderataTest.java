package org.cswteams.ms3.control.desiderata;

import org.cswteams.ms3.dto.DesiderataDTO;
import org.cswteams.ms3.exception.DatabaseException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Profile("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD) // The spring application context will be considered "dirty" before each test method, and will be rebuilt.
public class ControllerDesiderataTest {
/*
    @Autowired
    private IControllerDesiderata controllerDesiderata;

    @Autowired
    private UtenteDao utenteDao;



    @Test
    @Transactional
    public void testAggiungiDesiderata1() throws DatabaseException {
        //VARIABILI UTILI
        Desiderata desiderata;

        //CREAZIONE DI UN NUOVO DTO DESIDERATA + IDENTIFICAZIONE DELL'UTENTE DA COINVOLGERE
        DesiderataDTO dto = new DesiderataDTO(LocalDate.of(2024, 2, 16));
        long utenteId = utenteDao.findByEmailAndPassword("domenicoverde@gmail.com", "passw").getId();

        //INVOCAZIONE DEL CONTROLLER APPLICATIVO PER AGGIUNGERE UN DESIDERATA + CHECK SULL'ENTITY DESIDERATA RESTITUITA
        desiderata = this.controllerDesiderata.aggiungiDesiderata(dto, utenteId);

        Assert.assertEquals(16, desiderata.getData().getDayOfMonth());
        Assert.assertEquals(2, desiderata.getData().getMonthValue());
        Assert.assertEquals(2024, desiderata.getData().getYear());
        Assert.assertEquals(utenteId, (long) desiderata.getUtente().getId());

    }



    @Test
    @Transactional
    public void testAggiungiDesiderata2() throws DatabaseException {     //NB: BUG - findByEmailAndPassword() returns multiple results.
        //VARIABILI UTILI
        Desiderata desiderata;

        //CREAZIONE DI UN NUOVO DTO DESIDERATA + IDENTIFICAZIONE DELL'UTENTE DA COINVOLGERE
        DesiderataDTO dto = new DesiderataDTO(LocalDate.of(2024, 2, 16));
        long utenteId = utenteDao.findByEmailAndPassword("***@gmail.com@gmail.com", "passw").getId();

        //INVOCAZIONE DEL CONTROLLER APPLICATIVO PER AGGIUNGERE UN DESIDERATA + CHECK SULL'ENTITY DESIDERATA RESTITUITA
        desiderata = this.controllerDesiderata.aggiungiDesiderata(dto, utenteId);

        Assert.assertEquals(16, desiderata.getData().getDayOfMonth());
        Assert.assertEquals(2, desiderata.getData().getMonthValue());
        Assert.assertEquals(2024, desiderata.getData().getYear());
        Assert.assertEquals(utenteId, (long) desiderata.getUtente().getId());

    }



    @Test(expected = DatabaseException.class)
    @Transactional
    public void testAggiungiDesiderataFail() throws DatabaseException {
        //CREAZIONE DI UN NUOVO DTO DESIDERATA + DEFINIZIONE DI UN UTENTE ID INESISTENTE
        DesiderataDTO dto = new DesiderataDTO(LocalDate.of(2024, 2, 16));
        long utenteId = -100;

        //INVOCAZIONE DEL CONTROLLER APPLICATIVO (CHE DOVREBBE LANCIARE L'ECCEZIONE)
        this.controllerDesiderata.aggiungiDesiderata(dto, utenteId);

    }



    @Test
    @Transactional
    public void testCancellaDesiderata() throws DatabaseException {
        //VARIABILI UTILI
        Desiderata desiderata;

        //CREAZIONE DI UN NUOVO DTO DESIDERATA + IDENTIFICAZIONE DELL'UTENTE DA COINVOLGERE
        DesiderataDTO dto = new DesiderataDTO(LocalDate.of(2024, 2, 16));
        long utenteId = utenteDao.findByEmailAndPassword("domenicoverde@gmail.com", "passw").getId();

        //INVOCAZIONE DEL CONTROLLER APPLICATIVO PER AGGIUNGERE UN DESIDERATA E POI RIMUOVERLO
        desiderata = this.controllerDesiderata.aggiungiDesiderata(dto, utenteId);
        this.controllerDesiderata.cancellaDesiderata(desiderata.getId(), utenteId); //è sufficiente che non venga sollevata alcuna eccezione.

    }



    @Test(expected = DatabaseException.class)
    @Transactional
    public void testCancellaDesiderataFail() throws DatabaseException {
        //VARIABILI UTILI
        Desiderata desiderata;

        //CREAZIONE DI UN NUOVO DTO DESIDERATA + IDENTIFICAZIONE DELL'UTENTE DA COINVOLGERE
        DesiderataDTO dto = new DesiderataDTO(LocalDate.of(2024, 2, 16));
        long utenteId = utenteDao.findByEmailAndPassword("domenicoverde@gmail.com", "passw").getId();

        //INVOCAZIONE DEL CONTROLLER APPLICATIVO PER AGGIUNGERE UN DESIDERATA E POI TENTARE DI RIMUOVERE UN DESIDERATA RELATIVO A UN UTENTE INESISTENTE
        desiderata = this.controllerDesiderata.aggiungiDesiderata(dto, utenteId);
        this.controllerDesiderata.cancellaDesiderata(desiderata.getId(), utenteId*(-1));

    }



    @Test
    @Transactional
    public void testAggiungiDesiderate() throws DatabaseException {
        //VARIABILI UTILI
        List<DesiderataDTO> dtos = new ArrayList<>();
        List<Desiderata> listaDesiderata;

        //CREAZIONE DI ALCUNI DTO DESIDERATA + IDENTIFICAZIONE DELL'UTENTE DA COINVOLGERE
        dtos.add(new DesiderataDTO(LocalDate.of(2024, 2, 16)));
        dtos.add(new DesiderataDTO(LocalDate.of(2024, 5, 26)));
        long utenteId = utenteDao.findByEmailAndPassword("domenicoverde@gmail.com", "passw").getId();

        //INVOCAZIONE DEL CONTROLLER APPLICATIVO PER AGGIUNGERE I DESIDERATA + CHECK SUI DESIDERATA RESTITUITI
        listaDesiderata = this.controllerDesiderata.aggiungiDesiderate(dtos, utenteId);

        Assert.assertEquals(16, listaDesiderata.get(0).getData().getDayOfMonth());
        Assert.assertEquals(2, listaDesiderata.get(0).getData().getMonthValue());
        Assert.assertEquals(2024, listaDesiderata.get(0).getData().getYear());
        Assert.assertEquals(utenteId, (long) listaDesiderata.get(0).getUtente().getId());

        Assert.assertEquals(26, listaDesiderata.get(1).getData().getDayOfMonth());
        Assert.assertEquals(5, listaDesiderata.get(1).getData().getMonthValue());
        Assert.assertEquals(2024, listaDesiderata.get(1).getData().getYear());
        Assert.assertEquals(utenteId, (long) listaDesiderata.get(1).getUtente().getId());

    }
*/
}
