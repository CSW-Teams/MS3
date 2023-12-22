package org.cswteams.ms3.control.servizi;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Profile("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class ControllerServiziTests /*extends ControllerSchedulerTestEnv*/ {
   /* @Autowired
    private ControllerServizi instance;

    @Autowired
    protected ServizioDao servizioDao;

    public Stream<String> servizioValidNamesParams() {
        List<String> servizioList = this.servizioDao.findAll()
                .stream().map(Servizio::getNome).collect(Collectors.toUnmodifiableList());
        return servizioList.stream();
    }

    @Test
    public void createServizioTest() {
        String servizioName = "ServizioTest";
        List<MansioneEnum> mansioni = new ArrayList<>();
        mansioni.add(MansioneEnum.REPARTO);
        mansioni.add(MansioneEnum.AMBULATORIO);
        ServizioDTO servizioDTO = new ServizioDTO(servizioName, mansioni);
        Servizio servizio = this.instance.creaServizio(servizioDTO);
        Assert.assertNotNull(servizio);
        Assert.assertEquals(servizioName, servizio.getNome());
        Assert.assertEquals(2, servizio.getMansioni().size());
        for (MansioneEnum mansione : servizio.getMansioni()) {
            Assert.assertTrue(mansioni.contains(mansione));
        }
    }

    @Test
    public void readAllServizioTest() {
        Set<ServizioDTO> servizi = this.instance.leggiServizi();
        Assert.assertNotNull(servizi);
        Assert.assertEquals(servizioDao.findAll().size(), servizi.size());
    }

    @ParameterizedTest
    @MethodSource(value = "servizioValidNamesParams")
    public void readServizioByNameValidTest(String name) {
        ServizioDTO servizio = this.instance.leggiServizioByNome(name);
        Assert.assertNotNull(servizio);
        Assert.assertEquals(name, servizio.getNome());
    }*/

    /**
     * This test is duplicated for any possible future purposes,
     * e.g. different treatments for invalid values w.r.t. null values
     */
    /*@ParameterizedTest
    @ValueSource(strings = {"CostruzioneDelSoftware"})
    @EmptySource
    public void readServizioByNameInvalidTest(String name) {
        Assertions.assertThrows(Exception.class, () -> this.instance.leggiServizioByNome(name));
    }*/

    /**
     * This test is duplicated for any possible future purposes,
     * e.g. different treatments for invalid values w.r.t. null values
     */
    /*@ParameterizedTest
    @NullSource
    public void readServizioByNameExceptionsTest(String name) {
        Assertions.assertThrows(Exception.class, () -> this.instance.leggiServizioByNome(name));
    }*/
}

