package org.cswteams.ms3.control.vincoli;

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
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD) // The spring application context will be considered "dirty" before each test method, and will be rebuilt.
public class ControllerVincoloTest {
/*
    @Autowired
    private IControllerVincolo controllerVincolo;

    @Autowired
    private CategorieDao categorieDao;

    @Autowired
    private ConfigVincoloMaxPeriodoConsecutivoDAO configVincoloMaxPeriodoConsecutivoDao;

    @Autowired
    private VincoloDao vincoloDao;



    @Before
    public void populateDBTestVincolo() {
        //CREAZIONE DELLE ENTITA' VINCOLI
        //VincoloTipologieTurniContigue
        VincoloTipologieTurniContigue vincoloTurniContigui = new VincoloTipologieTurniContigue(
                20,
                ChronoUnit.HOURS,
                TipologiaTurno.NOTTURNO,
                new HashSet<>(Arrays.asList(TipologiaTurno.values()))
        );

        //VincoloMaxOrePeriodo
        VincoloMaxOrePeriodo vincoloMaxOrePeriodo = new VincoloMaxOrePeriodo(7, 4800);

        //VincoloMaxPeriodoConsecutivo
        VincoloMaxPeriodoConsecutivo vincoloPeriodoConsecutivoOver62 = new VincoloMaxPeriodoConsecutivo(
                360,
                categorieDao.findAllByNome("OVER_62")
        );
        VincoloMaxPeriodoConsecutivo vincoloPeriodoConsecutivoIncinta = new VincoloMaxPeriodoConsecutivo(
                360,
                categorieDao.findAllByNome("INCINTA")
        );
        List<Vincolo> vincoliMaxPeriodoConsecutivo = new LinkedList<>(Arrays.asList(vincoloPeriodoConsecutivoOver62, vincoloPeriodoConsecutivoIncinta));

        //FLUSH NEL DB DELLE ENTITA' VINCOLI APPENA CREATE
        vincoloDao.saveAndFlush(vincoloTurniContigui);
        vincoloDao.saveAll(vincoliMaxPeriodoConsecutivo);
        vincoloDao.saveAndFlush(vincoloMaxOrePeriodo);

    }



    @Test
    @Transactional
    public void testControllerVincolo() {
        //UPDATE DELLE ENTITA' CONFIGVINCOLOMAXPERIODOCONSECUTIVO
        ConfigVincoloMaxPeriodoConsecutivo confOver62 = configVincoloMaxPeriodoConsecutivoDao.findAllByCategoriaVincolataNome("OVER_62").get(0);
        confOver62.setNumMaxMinutiConsecutivi(390);
        ConfigVincoloMaxPeriodoConsecutivo confIncinta = configVincoloMaxPeriodoConsecutivoDao.findAllByCategoriaVincolataNome("INCINTA").get(0);
        confIncinta.setNumMaxMinutiConsecutivi(390);

        configVincoloMaxPeriodoConsecutivoDao.saveAndFlush(confOver62);
        configVincoloMaxPeriodoConsecutivoDao.saveAndFlush(confIncinta);

        //CREAZIONE DI UN'ISTANZA CONFIGVINCOLI DA PASSARE COME PARAMETRO AL CONTROLLER UNDER TEST
        ConfigVincoli configVincoli = new ConfigVincoli(
                7,
                4200,
                20,
                780,
                Arrays.asList(confOver62, confIncinta)
        );

        //INVOCAZIONE DEL CONTROLLER APPLICATIVO PER AGGIORNARE I VINCOLI
        this.controllerVincolo.aggiornaVincoli(configVincoli);

        //CHECK SUL CORRETTO AGGIORNAMENTO DEI VINCOLI
        List<Vincolo> vincoliAggiornati = this.controllerVincolo.leggiVincoli();
        System.out.println(vincoliAggiornati);

        //VincoloTipologieTurniContigue
        VincoloTipologieTurniContigue vincoloTurniContiguiAggiornato = (VincoloTipologieTurniContigue) vincoliAggiornati.get(0);
        Assert.assertEquals(20, vincoloTurniContiguiAggiornato.getHorizon());

        VincoloMaxOrePeriodo vincoloMaxOrePeriodo = (VincoloMaxOrePeriodo) vincoliAggiornati.get(6);
        Assert.assertEquals(7, vincoloMaxOrePeriodo.getNumGiorniPeriodo());
        Assert.assertEquals(4200, vincoloMaxOrePeriodo.getNumMinutiMaxPeriodo());

        VincoloMaxPeriodoConsecutivo vincoloMaxPeriodoConsecutivoAggiornato1 = (VincoloMaxPeriodoConsecutivo) vincoliAggiornati.get(9);
        Assert.assertEquals(390, vincoloMaxPeriodoConsecutivoAggiornato1.getMaxConsecutiveMinutes());
        Assert.assertEquals("OVER_62", vincoloMaxPeriodoConsecutivoAggiornato1.getCategoriaVincolata().getNome());
        Assert.assertEquals(TipoCategoriaEnum.STATO, vincoloMaxPeriodoConsecutivoAggiornato1.getCategoriaVincolata().getTipo());
        VincoloMaxPeriodoConsecutivo vincoloMaxPeriodoConsecutivoAggiornato2 = (VincoloMaxPeriodoConsecutivo) vincoliAggiornati.get(10);
        Assert.assertEquals(390, vincoloMaxPeriodoConsecutivoAggiornato2.getMaxConsecutiveMinutes());
        Assert.assertEquals("INCINTA", vincoloMaxPeriodoConsecutivoAggiornato2.getCategoriaVincolata().getNome());
        Assert.assertEquals(TipoCategoriaEnum.STATO, vincoloMaxPeriodoConsecutivoAggiornato2.getCategoriaVincolata().getTipo());
        VincoloMaxPeriodoConsecutivo vincoloMaxPeriodoConsecutivoAggiornato3 = (VincoloMaxPeriodoConsecutivo) vincoliAggiornati.get(11);
        Assert.assertEquals(780, vincoloMaxPeriodoConsecutivoAggiornato3.getMaxConsecutiveMinutes());
        Assert.assertNull(vincoloMaxPeriodoConsecutivoAggiornato3.getCategoriaVincolata());

    }*/

}
