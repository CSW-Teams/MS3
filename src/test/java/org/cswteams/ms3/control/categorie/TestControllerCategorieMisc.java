package org.cswteams.ms3.control.categorie;

import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD) // The spring application context will be considered "dirty" before each test method, and will be rebuilt. It means that
@ActiveProfiles(value = "test")
public class TestControllerCategorieMisc {
/*    @Autowired
    private IControllerCategorie controller ;

    @Autowired
    private CategorieDao dao ;

    @Test
    public void testNullCategoryInsertion() {

        try {
            dao.saveAndFlush(new Categoria()) ;

            List<Categoria> categorie = dao.findAll() ;

            if(categorie.size() == 1)
            {
                Categoria categoria = categorie.get(0) ;

                if(categoria.getNome() == null && categoria.getTipo() == null && controller.leggiCategorieStato().isEmpty()
                && controller.leggiCategorieSpecializzazioni().isEmpty() && controller.leggiCategorieTurnazioni().isEmpty())
                    fail() ;
            } else if(categorie.isEmpty()) {
                return ;
            } else {
                fail() ;
            }

        } catch (Exception e) {
            return;
        }

        fail() ;
    }

    @Test
    public void testSameNameDifferentCategories() {
        dao.save(new Categoria("A", TipoCategoriaEnum.STATO)) ;
        dao.save(new Categoria("A", TipoCategoriaEnum.TURNAZIONE)) ;
        dao.save(new Categoria("A", TipoCategoriaEnum.SPECIALIZZAZIONE)) ;

        Set<CategoriaDTO> dto ;

        try {
            dto = controller.leggiCategorieStato() ;
            if(!(dto.size() == 1 && dto.contains(new CategoriaDTO("A", TipoCategoriaEnum.STATO)))) fail() ;

            dto = controller.leggiCategorieSpecializzazioni() ;
            if(!(dto.size() == 1 && dto.contains(new CategoriaDTO("A", TipoCategoriaEnum.SPECIALIZZAZIONE)))) fail() ;

            dto = controller.leggiCategorieTurnazioni() ;
            if(!(dto.size() == 1 && dto.contains(new CategoriaDTO("A", TipoCategoriaEnum.TURNAZIONE)))) fail() ;
        } catch (ParseException e)
        {
            fail() ;
        }

    }

    @After
    public void cleanAll() {
        dao.deleteAll() ;
    }*/
}
