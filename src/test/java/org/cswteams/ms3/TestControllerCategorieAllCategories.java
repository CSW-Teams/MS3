package org.cswteams.ms3;

import org.cswteams.ms3.control.categorie.IControllerCategorie;
import org.cswteams.ms3.dao.CategorieDao;
import org.cswteams.ms3.dto.CategoriaDTO;
import org.cswteams.ms3.entity.Categoria;
import org.cswteams.ms3.enums.TipoCategoriaEnum;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.transaction.Transactional;
import java.text.ParseException;
import java.util.Set;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD) // The spring application context will be considered "dirty" before each test method, and will be rebuilt. It means that
@Transactional
@ActiveProfiles(value = "test")
public class TestControllerCategorieAllCategories {

    @Autowired
    private IControllerCategorie controller ;

    @Autowired
    private CategorieDao categorieDao ;

    @Before
    public void prepareDB() {

        categorieDao.save(new Categoria("A", TipoCategoriaEnum.STATO)) ;
        categorieDao.save(new Categoria("B", TipoCategoriaEnum.STATO)) ;

        categorieDao.save(new Categoria("C", TipoCategoriaEnum.TURNAZIONE)) ;
        categorieDao.save(new Categoria("D", TipoCategoriaEnum.TURNAZIONE)) ;

        categorieDao.save(new Categoria("E", TipoCategoriaEnum.SPECIALIZZAZIONE)) ;
        categorieDao.save(new Categoria("F", TipoCategoriaEnum.SPECIALIZZAZIONE)) ;
    }

    @Test
    public void testAllCategoriesSaved() {

        Set<CategoriaDTO> statoDto, turnazioneDto, specializzazioneDto ;

        try {
            statoDto = controller.leggiCategorieStato() ;
            turnazioneDto = controller.leggiCategorieTurnazioni() ;
            specializzazioneDto = controller.leggiCategorieSpecializzazioni() ;

            assertEquals(2, statoDto.size());
            assertEquals(2, turnazioneDto.size());
            assertEquals(2, specializzazioneDto.size());

            assertTrue(statoDto.contains(new CategoriaDTO("A", TipoCategoriaEnum.STATO)));
            assertTrue(statoDto.contains(new CategoriaDTO("B", TipoCategoriaEnum.STATO)));
            assertTrue(turnazioneDto.contains(new CategoriaDTO("C", TipoCategoriaEnum.TURNAZIONE)));
            assertTrue(turnazioneDto.contains(new CategoriaDTO("D", TipoCategoriaEnum.TURNAZIONE)));
            assertTrue(specializzazioneDto.contains(new CategoriaDTO("E", TipoCategoriaEnum.SPECIALIZZAZIONE)));
            assertTrue(specializzazioneDto.contains(new CategoriaDTO("F", TipoCategoriaEnum.SPECIALIZZAZIONE)));

        } catch (ParseException e) {
            fail() ;
        }
    }
}
