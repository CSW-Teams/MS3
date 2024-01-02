package org.cswteams.ms3.control.categorie;

import lombok.RequiredArgsConstructor;
import org.cswteams.ms3.control.categorie.IControllerCategorie;
import org.cswteams.ms3.dao.CategorieDao;
import org.cswteams.ms3.dto.CategoriaDTO;
import org.cswteams.ms3.entity.Categoria;
import org.cswteams.ms3.enums.TipoCategoriaEnum;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.junit4.rules.SpringClassRule;
import org.springframework.test.context.junit4.rules.SpringMethodRule;

import javax.transaction.Transactional;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Set;

import static org.junit.Assert.*;

@RunWith(Parameterized.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD) // The spring application context will be considered "dirty" before each test method, and will be rebuilt. It means that
@Transactional
@ActiveProfiles(value = "test")
public class TestControllerCategorieOnlyOneKind {

    @ClassRule
    public static final SpringClassRule scr = new SpringClassRule();

    @Rule
    public final SpringMethodRule smr = new SpringMethodRule();

    @Autowired
    private CategorieDao categorieDao ;

    @Autowired
    private IControllerCategorie controller ;

    private final TipoCategoriaEnum categoria ;

    private final int categorySize ;

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
                { TipoCategoriaEnum.STATO, 1 },
                { TipoCategoriaEnum.STATO, 10 },
                { TipoCategoriaEnum.SPECIALIZZAZIONE, 1 },
                { TipoCategoriaEnum.SPECIALIZZAZIONE, 10 },
                { TipoCategoriaEnum.TURNAZIONE, 1 },
                { TipoCategoriaEnum.STATO, 10 },
        });
    }

    public TestControllerCategorieOnlyOneKind(TipoCategoriaEnum categoria, int categorySize) {
        this.categoria = categoria;
        this.categorySize = categorySize;
    }

    @Before
    public void initializeDB() {

        for (int i = 0 ; i < categorySize ; i++) {

            categorieDao.save(new Categoria(String.format("%d", i), categoria)) ;
        }
    }

    @Test
    public void testLeggiCategoriaOnlyOneKind() {

        Set<CategoriaDTO> dtos ;
        Set<CategoriaDTO> otherdtos ;

        try {
            switch (categoria) {
                case STATO:
                    otherdtos = controller.leggiCategorieSpecializzazioni() ;
                    otherdtos.addAll(controller.leggiCategorieTurnazioni()) ;
                    dtos = controller.leggiCategorieStato() ;
                    break;
                case SPECIALIZZAZIONE:
                    otherdtos = controller.leggiCategorieStato() ;
                    otherdtos.addAll(controller.leggiCategorieTurnazioni()) ;
                    dtos = controller.leggiCategorieSpecializzazioni() ;
                    break;
                case TURNAZIONE:
                    otherdtos = controller.leggiCategorieStato() ;
                    otherdtos.addAll(controller.leggiCategorieSpecializzazioni()) ;
                    dtos = controller.leggiCategorieTurnazioni() ;
                    break;
                default:
                    fail() ;
                    return ;
            }



            assertEquals(0, otherdtos.size()) ;

            assertEquals(categorySize, dtos.size());

            for (int i = 0; i < categorySize ; i++)
            {
                assertTrue(dtos.contains(new CategoriaDTO(String.format("%d", i), categoria)));
            }

        } catch (ParseException e) {
            fail() ;
        }
    }
}
