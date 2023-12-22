package org.cswteams.ms3.control.categorie;

import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.transaction.Transactional;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD) // The spring application context will be considered "dirty" before each test method, and will be rebuilt. It means that
@Transactional
@ActiveProfiles(value = "test")
public class TestControllerCategorieNoCategories {
/*
    @Autowired
    private IControllerCategorie controller ;

    @Test
    public void testLeggiCategoriaStatoNoCategories() {

        try {
            Set<CategoriaDTO> dtos = controller.leggiCategorieStato() ;

            assertEquals(0, dtos.size()) ;

        } catch (ParseException e) {
            fail() ;
        }
    }

    @Test
    public void testLeggiCategoriaSpecializzazioniNoCategories() {

        try {
            Set<CategoriaDTO> dtos = controller.leggiCategorieSpecializzazioni() ;

            assertEquals(0, dtos.size()) ;

        } catch (ParseException e) {
            fail() ;
        }
    }

    @Test
    public void testLeggiCategoriaTurnazioniNoCategories() {

        try {
            Set<CategoriaDTO> dtos = controller.leggiCategorieTurnazioni() ;

            assertEquals(0, dtos.size()) ;

        } catch (ParseException e) {
            fail() ;
        }
    }*/
}
