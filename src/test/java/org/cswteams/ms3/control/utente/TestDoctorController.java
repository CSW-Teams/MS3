package org.cswteams.ms3.control.utente;

import org.cswteams.ms3.dao.UtenteDao;
import org.cswteams.ms3.dto.UtenteDTO;
import org.cswteams.ms3.enums.AttoreEnum;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.transaction.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD) // The spring application context will be considered "dirty" before each test method, and will be rebuilt. It means that
@Transactional
@ActiveProfiles(value = "test")
public class TestDoctorController {

    @Autowired
    private ControllerUtente controller ;

    @Autowired
    private UtenteDao dao ;

    @Test
    public void testNullUtente() {
        UtenteDTO dto = new UtenteDTO() ;

        try {
            controller.creaUtente(dto) ;
        } catch (Exception e)
        {
            return;
        }

        fail() ;
    }

    @Test
    public void testNullAnagrafica() {
        UtenteDTO dto = new UtenteDTO() ;

        dto.setAttore(AttoreEnum.UTENTE) ;
        dto.setEmail("e.mail@mail.it") ;
        dto.setPassword("passw") ;

        try {
            controller.creaUtente(dto) ;
        } catch (Exception e) {
            return;
        }

        fail() ;
    }

    @Test
    public void testNullCredenziali() {
        UtenteDTO dto = new UtenteDTO() ;

        dto.setAttore(AttoreEnum.PIANIFICATORE) ;
        dto.setCognome("ggfgf");
        dto.setNome("djfjjf");
        dto.setCodiceFiscale("Iirvcnn");
        dto.setDataNascita(LocalDate.of(2000, 12, 23)) ;
        dto.setCategorie(new ArrayList<>()) ;
        dto.setSpecializzazioni(new ArrayList<>()); ;

        try {
            controller.creaUtente(dto) ;
        } catch (Exception e) {
            return;
        }

        fail() ;
    }

    @Test
    public void multipleUsersSameMail() {
        UtenteDTO dto = new UtenteDTO() ;

        dto.setAttore(AttoreEnum.UTENTE) ;
        dto.setEmail("e.mail@mail.it") ;
        dto.setPassword("passw") ;

        controller.creaUtente(dto) ;
        dto.setPassword("2jrfejbcn") ;
        controller.creaUtente(dto) ;

        Set<UtenteDTO> users =  controller.leggiUtenti() ;
        List<UtenteDTO> usersConverted = List.copyOf(users) ;

        assertNotEquals(usersConverted.get(0).getPassword(), usersConverted.get(1).getPassword());
        assertEquals(usersConverted.get(0).getEmail(), usersConverted.get(1).getEmail());

    }

    @Test
    public void testUtenteWithoutRoleAndNullPassword() {
        UtenteDTO dto = new UtenteDTO() ;
        dto.setNome("Romolo");
        dto.setCognome("Di Giuseppe");
        dto.setEmail("djkedn");
        dto.setDataNascita(LocalDate.of(2000, 12, 12));
        dto.setPassword("");
        dto.setId(2L) ;

        try {
            controller.creaUtente(dto) ;
        } catch (Exception e) {
            return;
        }

        fail();
    }

    @Test
    public void testAssignWrongCategories() {
        UtenteDTO dto = new UtenteDTO() ;

        CategoriaUtente cu = new CategoriaUtente(new Categoria("Specializz.1", TipoCategoriaEnum.SPECIALIZZAZIONE),
                LocalDate.of(2000, 3, 3), LocalDate.of(2023, 12, 12)) ;

        CategoriaUtente cu2 ;

        try {
            cu2 = new CategoriaUtente(new Categoria("Stato1", TipoCategoriaEnum.STATO),
                    LocalDate.of(2024, 3, 3), LocalDate.of(2023, 12, 12)) ;

            dto.setCategorie(List.of(cu, cu2)) ;
            controller.creaUtente(dto) ;


        } catch (Exception e) {
            return;
        }

        fail();
    }

    @Test
    public void testFetchZeroUsers() {
        assertTrue(controller.leggiUtenti().isEmpty()) ;
    }

    @Test
    public void testFetchUnexistingUser() {
        try {
            assertNull(controller.leggiUtente(34));
        } catch (Exception e) {
            fail();
        }

    }

    @After
    public void clean() {
        dao.deleteAll() ;
    }
}
