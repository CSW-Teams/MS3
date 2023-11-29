package org.cswteams.ms3.entity;

import org.cswteams.ms3.dao.CategorieDao;
import org.cswteams.ms3.enums.TipoCategoriaEnum;
import org.junit.After;
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
import org.springframework.test.context.junit4.rules.SpringClassRule;
import org.springframework.test.context.junit4.rules.SpringMethodRule;

import java.util.Collection;
import java.util.List;

import static org.junit.Assert.fail;

@RunWith(Parameterized.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD) // The spring application context will be considered "dirty" before each test method, and will be rebuilt. It means that
@ActiveProfiles(value = "test")
public class TestCategoriaRobustness {

    @ClassRule
    public static final SpringClassRule scr = new SpringClassRule();

    @Rule
    public final SpringMethodRule smr = new SpringMethodRule();

    @Autowired
    private CategorieDao dao ;

    private Categoria toTest ;

    private boolean positive ;

    public TestCategoriaRobustness(Categoria categoria, boolean positive) {
        this.toTest = categoria ;
        this.positive = positive ;
    }

    @Parameterized.Parameters
    public static Collection<Object[]> params() {

        return List.of(
                new Object[] {new Categoria(null, null), false},
                new Object[] {new Categoria(null, TipoCategoriaEnum.STATO), false},
                new Object[] {new Categoria(null, TipoCategoriaEnum.SPECIALIZZAZIONE), false},
                new Object[] {new Categoria(null, TipoCategoriaEnum.TURNAZIONE), false},
                new Object[] {new Categoria("A", null), false},
                new Object[] {new Categoria("A", TipoCategoriaEnum.STATO), true},
                new Object[] {new Categoria("A", TipoCategoriaEnum.SPECIALIZZAZIONE), true},
                new Object[] {new Categoria("A", TipoCategoriaEnum.TURNAZIONE), true}
        );
    }

    @Test
    public void testCategorie() {
        if(positive) {
            try {
                dao.saveAndFlush(toTest) ;
            } catch (Exception e) {
                fail() ;
            }
        } else {
            try {
                dao.saveAndFlush(toTest) ;
            } catch (Exception e) {
                return;
            }
            fail() ;
        }
    }

    @After
    public void clean() {
        dao.deleteAll() ;
    }
}
