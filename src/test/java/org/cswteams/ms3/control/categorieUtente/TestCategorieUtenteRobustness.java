package org.cswteams.ms3.control.categorieUtente;


import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import javax.transaction.Transactional;

@RunWith(Parameterized.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD) // The spring application context will be considered "dirty" before each test method, and will be rebuilt. It means that
@Transactional
@ActiveProfiles(value = "test")
public class TestCategorieUtenteRobustness {
/*    @ClassRule
    public static final SpringClassRule scr = new SpringClassRule();

    @Rule
    public final SpringMethodRule smr = new SpringMethodRule();

    @Autowired
    private CategoriaUtenteDao dao ;

    @Autowired
    private CategorieDao categorieDao ;

    private final CategoriaUtente toSave ;

    private final boolean positive;

    public TestCategorieUtenteRobustness(CategoriaUtente toSave, boolean positive) {
        this.toSave = toSave ;
        this.positive = positive;
    }

    @Parameterized.Parameters
    public static Collection<Object[]> data() {

        LocalDate today = LocalDate.now() ;

        Collection<Object[]> retVal = new ArrayList<>() ;

        List<Categoria> categories = new ArrayList<>(List.of(
                new Categoria(null, null),
                new Categoria("A", TipoCategoriaEnum.STATO),
                new Categoria("A", TipoCategoriaEnum.TURNAZIONE),
                new Categoria("A", TipoCategoriaEnum.SPECIALIZZAZIONE)
        ));

        categories.add(null) ;

        List<LocalDate> befores = new ArrayList<>(List.of(
                today.minusYears(3),
                today.plusYears(3)
        ));

        befores.add(null) ;

        for (Categoria categoria : categories) {
            for(LocalDate before : befores) {

                CategoriaUtente categoriaUtente1 ;
                CategoriaUtente categoriaUtente2, categoriaUtente3 = null, categoriaUtente4 ;

                if(before != null) {

                    if(before.isAfter(today)) {
                        categoriaUtente1 = new CategoriaUtente(categoria, before, today.minusMonths(4)) ;
                    }
                    else {
                        categoriaUtente1 = new CategoriaUtente(categoria, before, today.plusMonths(4)) ;
                    }
                    categoriaUtente2 = new CategoriaUtente(categoria, before, before.minusMonths(4)) ;
                    categoriaUtente3 = new CategoriaUtente(categoria, before, before.plusMonths(4)) ;

                } else {
                    categoriaUtente1 = new CategoriaUtente(categoria, null, today.minusMonths(4)) ;
                    categoriaUtente2 = new CategoriaUtente(categoria, null, today.plusMonths(4)) ;
                }

                categoriaUtente4 = new CategoriaUtente(categoria, before, null) ;

                if(categoria != null && categoria.getNome() != null && categoria.getTipo() != null && before != null) {

                    if(before.isAfter(today))
                        retVal.add(new Object[]{
                                categoriaUtente1, false
                        }) ;
                    else
                        retVal.add(new Object[]{
                                categoriaUtente1, true
                        }) ;

                    retVal.add(new Object[]{
                            categoriaUtente3, true
                    }) ;
                } else {
                    retVal.add(new Object[]{
                            categoriaUtente1, false
                    }) ;

                    if(before != null)
                        retVal.add(new Object[]{
                                categoriaUtente3, false
                        }) ;
                }

                retVal.add(new Object[]{
                        categoriaUtente2, false
                }) ;

                retVal.add(new Object[]{
                        categoriaUtente4, false
                }) ;
            }
        }

        return retVal ;
    }

    @Test
    public void testCategoriaUtenteRobustness() {

        if(positive) {
            try {
                if(toSave.getCategoria() != null) {
                    categorieDao.saveAndFlush(toSave.getCategoria()) ;
                }
                dao.saveAndFlush(toSave) ;
            } catch (Exception e) {
                fail() ;
            }
        } else
        {
            try {
                if(toSave.getCategoria() != null) {
                    categorieDao.saveAndFlush(toSave.getCategoria()) ;
                }
                dao.saveAndFlush(toSave) ;
            } catch (Exception e) {
                return;
            }

            fail() ;
        }
    }*/
}
