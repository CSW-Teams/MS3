package org.cswteams.ms3.sprint0plus;

import org.cswteams.ms3.control.servizi.ControllerServizi;
import org.cswteams.ms3.dto.ServizioDTO;
import org.cswteams.ms3.entity.Servizio;
import org.cswteams.ms3.enums.MansioneEnum;
import org.cswteams.ms3.sprint0plus.utils.TestEnv;
import org.junit.Assert;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Profile("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class ControllerServiziTests extends TestEnv {
    @Autowired
    private ControllerServizi instance;

    @Test
    public void createServizioTest() {
        //TODO mock?
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
    @ValueSource(strings = "cardiologia")
    public void readServizioByNameValidTest(String name) {
        ServizioDTO servizio = this.instance.leggiServizioByNome(name);
        Assert.assertNotNull(servizio);
        Assert.assertEquals(name, servizio.getNome());
    }

    /**
     * This test is duplicated for any possible future purposes,
     * e.g. different treatments for invalid values w.r.t. null values
     */
    @ParameterizedTest
    @ValueSource(strings = {"CostruzioneDelSoftware"})
    @EmptySource
    public void readServizioByNameInvalidTest(String name) {
        Assertions.assertThrows(Exception.class, () -> this.instance.leggiServizioByNome(name));
    }

    /**
     * This test is duplicated for any possible future purposes,
     * e.g. different treatments for invalid values w.r.t. null values
     */
    @ParameterizedTest
    @NullSource
    public void readServizioByNameExceptionsTest(String name) {
        Assertions.assertThrows(Exception.class, () -> this.instance.leggiServizioByNome(name));
    }
}

