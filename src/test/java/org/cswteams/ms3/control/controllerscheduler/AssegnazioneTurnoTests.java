package org.cswteams.ms3.control.controllerscheduler;

import org.junit.jupiter.api.TestInstance;
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Profile("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class AssegnazioneTurnoTests extends ControllerSchedulerTestEnv {
/*
    @Autowired
    AssegnazioneTurnoDao assegnazioneTurnoDao;

    @Autowired
    protected CategorieDao categorieDao;

    @Autowired
    protected TurnoDao turnoDao;

    @Autowired
    private ControllerServizi cs;

    @Autowired
    private ControllerTurni ct;

    @Autowired
    private VincoloDao vincoloDao;

    private RegistraAssegnazioneTurnoDTO registraAssegnazioneTurnoDTO;

    private Schedule testSchedule;

    private List<Utente> uGuardiaList;

    private List<Utente> uReperibiliList;

    @Before
    @BeforeEach
    public void envSetup() {
        vincoloDao.deleteAll();
        Utente uGuardia = new Utente("nome",
                "cognome",
                "codfis",
                LocalDate.of(1111, 11, 11),
                "mail@ms3.org",
                "",
                RuoloEnum.STRUTTURATO,
                AttoreEnum.UTENTE
        );
        utenteDao.saveAndFlush(uGuardia);
        uGuardiaList = new ArrayList<>();
        uGuardiaList.add(uGuardia);

        Utente uReperibili = new Utente("nome1",
                "cognome1",
                "codfis1",
                LocalDate.of(1112, 12, 12),
                "mail1@ms3.org",
                "",
                RuoloEnum.STRUTTURATO,
                AttoreEnum.UTENTE
        );
        utenteDao.saveAndFlush(uReperibili);
        uReperibiliList = new ArrayList<>();
        uReperibiliList.add(uReperibili);

        ServizioDTO servizioDTO1 = new ServizioDTO("ServizioTest", List.of(MansioneEnum.REPARTO));
        cs.creaServizio(servizioDTO1);

        TurnoDTO turnoDTO = new TurnoDTO();
        turnoDTO.setTimeSlot(TipologiaTurno.NOTTURNO);
        turnoDTO.setServizio(servizioDTO1);
        turnoDTO.setTask(MansioneEnum.REPARTO);
        Set<Categoria> categorieVietate = new HashSet<>();

        Categoria c = new Categoria("TEST", TipoCategoriaEnum.STATO);

        categorieVietate.add(c);
        categorieDao.saveAndFlush(c);
        turnoDTO.setCategorieVietate(categorieVietate);

        RuoloNumero rn = new RuoloNumero(RuoloEnum.STRUTTURATO, 1);
        turnoDTO.setRuoliNumero(List.of(rn));

        turnoDTO.setOraInizio(LocalTime.of(22, 0));
        turnoDTO.setDurata(Duration.ofHours(1));
        try {
            ct.creaTurno(turnoDTO);
        } catch (TurnoException e) {
            throw new RuntimeException(e);
        }

        this.testSchedule = this.instance.createSchedule(TODAY.getDate().plusYears(12), TODAY.getDate().plusYears(12).plusDays(5));
        this.instance.createSchedule(TODAY.getDate().plusDays(20), TODAY.getDate().plusDays(30));
        this.instance.createSchedule(TODAY.getDate().plusYears(30).plusMonths(1), TODAY.getDate().plusYears(30).plusMonths(1).plusDays(5));

        this.registraAssegnazioneTurnoDTO = new RegistraAssegnazioneTurnoDTO();
        this.registraAssegnazioneTurnoDTO.setServizio(this.cs.leggiServizioByNome("ServizioTest"));
        this.registraAssegnazioneTurnoDTO.setUtentiDiGuardia(MappaUtenti.utentiEntitytoDTO(uGuardiaList));
        this.registraAssegnazioneTurnoDTO.setUtentiReperibili(MappaUtenti.utentiEntitytoDTO(uReperibiliList));
        this.registraAssegnazioneTurnoDTO.setTipologiaTurno(TipologiaTurno.NOTTURNO);
        this.registraAssegnazioneTurnoDTO.setMansione(MansioneEnum.REPARTO);
        this.registraAssegnazioneTurnoDTO.setGiorno(LocalDate.now().plusDays(1));
    }

    public Stream<Arguments> assegnazioneTurnoInvalidParams() {
        return Stream.of(
                Arguments.of(new RegistraAssegnazioneTurnoDTO())//empty
        );
    }

    static Stream<Arguments> modificaAssegnazioneTurnoInvalidExceptionsParams() {
        return Stream.of(
                Arguments.of(new ModificaAssegnazioneTurnoDTO())

        );
    }

    @Test
    public void addAssegnazioneTurnoExternalValidTest() {
        // Compute the expectedAssegnazioniTurno value, needed for assertions below:
        // find the schedule (if any), that will contain the AssegnazioneTurno
        List<Schedule> scheduleList = scheduleDao.findAll();
        int ctr = 0;
        Schedule containingSchedule = null;
        LocalDate ratDate;
        ratDate = this.registraAssegnazioneTurnoDTO.getGiorno();
        for (Schedule s : scheduleList) {
            if (s.getStartDate().isBefore(ratDate.plusDays(1)) && s.getEndDate().isAfter(ratDate.minusDays(1))) {
                containingSchedule = s;
                // ... also, make sure that the schedule (if any) is only almost unique
                ctr++;
            }
        }
        int expectedAssegnazioniTurno = -1;

        // check containing schedule (almost-) uniqueness
        if (containingSchedule != null) {
            Assert.assertEquals(1, ctr);
        } else {
            Assert.assertEquals(0, ctr);
            containingSchedule = this.instance.createSchedule(ratDate.minusDays(1), ratDate.plusDays(3));
            Assert.assertNotNull(containingSchedule);
        }
        expectedAssegnazioniTurno = containingSchedule.getAssegnazioniTurno().size();

        // now run the actual test
        Schedule schedule = null;
        try {
            schedule = this.instance.aggiungiAssegnazioneTurno(this.registraAssegnazioneTurnoDTO, true);
        } catch (
                AssegnazioneTurnoException | IllegalScheduleException e) {
            throw new RuntimeException(e);
        }
        Assert.assertNotNull(schedule);

        for (Schedule a : scheduleList) {
            Assert.assertTrue(ScheduleTestUtils.overlapAllowanceCheck(a, schedule));
        }

        // check if the Turno was assigned
        boolean found = false;
        for (AssegnazioneTurno a : schedule.getAssegnazioniTurno()) {
            if (
                    this.registraAssegnazioneTurnoDTO.getTipologiaTurno().equals(a.getTurno().getTipologiaTurno())
                            && this.registraAssegnazioneTurnoDTO.getServizio().getNome().equals(MappaTurni.turnoEntityToDTO(a.getTurno()).getServizio().getNome())
                            && this.registraAssegnazioneTurnoDTO.getMansione().equals(MappaTurni.turnoEntityToDTO(a.getTurno()).getMansione())
                            && this.registraAssegnazioneTurnoDTO.getGiorno() == a.getData()
            ) {
                found = true;
                break;
            }
        }
        Assert.assertTrue(((schedule.isIllegal() && !found) ^ (!schedule.isIllegal() && found)));
        Assert.assertEquals(schedule.isIllegal() ? expectedAssegnazioniTurno : expectedAssegnazioniTurno + 1, schedule.getAssegnazioniTurno().size());
    }

    @ParameterizedTest
    @MethodSource(value = "assegnazioneTurnoInvalidParams")
    @NullSource
    public void addAssegnazioneTurnoExternalInvalidExceptionsTest(RegistraAssegnazioneTurnoDTO registraAssegnazioneTurnoDTO) {
        Assertions.assertThrows(Exception.class, () ->
                this.instance.aggiungiAssegnazioneTurno(registraAssegnazioneTurnoDTO, true)
        );
    }

    @Test
    public void updateAssegnazioneTurnoValidTest() {
        Long atId = this.testSchedule.getAssegnazioniTurno().get(0).getId();

        ModificaAssegnazioneTurnoDTO mat = new ModificaAssegnazioneTurnoDTO(
                atId,
                (uGuardiaList).stream().mapToLong(Utente::getId).toArray(),
                (uReperibiliList).stream().mapToLong(Utente::getId).toArray(),
                uGuardiaList.get(0).getId()
        );

        Schedule modifiedSchedule = null;
        try {
            modifiedSchedule = this.instance.modificaAssegnazioneTurno(mat);
        } catch (IllegalScheduleException e) {
            throw new RuntimeException(e);
        }
        Assert.assertNotNull(modifiedSchedule);

        // check if the AssegnazioneTurno still exists...
        Optional<AssegnazioneTurno> atRetrieved = assegnazioneTurnoDao
                .findById(modifiedSchedule.getAssegnazioniTurno().get(modifiedSchedule.getAssegnazioniTurno().size() - 1).getId());
        Assert.assertTrue(atRetrieved.isPresent());

        // ... and that its values are updated
        Assert.assertArrayEquals(uGuardiaList.toArray(), atRetrieved.get().getUtentiDiGuardia().toArray());
        Assert.assertArrayEquals(uReperibiliList.toArray(), atRetrieved.get().getUtentiReperibili().toArray());
    }

    @ParameterizedTest
    @MethodSource(value = "modificaAssegnazioneTurnoInvalidExceptionsParams")
    @NullSource
    public void updateAssegnazioneTurnoInvalidExceptionTest(ModificaAssegnazioneTurnoDTO mat) {
        Assertions.assertThrows(Exception.class, () -> this.instance.modificaAssegnazioneTurno(mat));
    }

    @Test
    public void removeAssegnazioneTurnoExternalValidTest() {
        Long id = this.testSchedule.getAssegnazioniTurno().get(0).getId();
        this.instance.rimuoviAssegnazioneTurno(id);
        Optional<AssegnazioneTurno> retrievedAt = assegnazioneTurnoDao.findById(id);
        Assert.assertFalse(retrievedAt.isPresent());
    }*/

    /**
     * (Domain partitioning/BVA) - Id management is totally handled by Spring/Hibernate, so it is
     * not (theoretically) possible to persist AssegnazioneTurno records with some "strange" Ids programmatically.
     * Hence, here we can only try to do it anyways, and check that Hibernate correctly reacts
     * to malformed requests.
     *
     * @param id
     */
    /*@ParameterizedTest
    @ValueSource(longs = {0, Long.MAX_VALUE})
    public void removeAssegnazioneTurnoExternalBoundaryValidTest(Long id) {
        Assert.assertEquals(Optional.empty(), this.assegnazioneTurnoDao.findById(id));
        Turno turno = this.turnoDao.findAll().get(0);
        LocalDate ratDate;
        ratDate = this.registraAssegnazioneTurnoDTO.getGiorno();
        AssegnazioneTurno at = new AssegnazioneTurno(ratDate, turno);
        at.setId(id);
        this.testSchedule = this.instance.createSchedule(TODAY.getDate().plusYears(12), TODAY.getDate().plusYears(12).plusDays(5));

        Assertions.assertThrows(Exception.class, () -> this.instance.aggiungiAssegnazioneTurno(registraAssegnazioneTurnoDTO, true));

        // this should not produce any effect into the db, since id management is handled by Spring/Hibernate
        this.assegnazioneTurnoDao.saveAndFlush(at);
        Assert.assertEquals(Optional.empty(), this.assegnazioneTurnoDao.findById(id));

        // ... hence, the removal should fail (=> false is returned)
        boolean ret = this.instance.rimuoviAssegnazioneTurno(id);
        Assert.assertFalse(ret);
    }*/

    /**
     * (Domain partitioning/BVA) - Id management is totally handled by Spring/Hibernate, so it is
     * not (theoretically) possible to persist AssegnazioneTurno records with some "strange" Ids programmatically.
     * Hence, here we can only try to do it anyways, and check that Hibernate correctly reacts
     * to malformed requests.
     *
     * @param id
     */
   /* @ParameterizedTest
    @ValueSource(longs = (-1))
    public void removeAssegnazioneTurnoExternalInvalidTest(Long id) {
        Assert.assertEquals(Optional.empty(), this.assegnazioneTurnoDao.findById(id));
        Turno turno = this.turnoDao.findAll().get(0);
        LocalDate ratDate;
        ratDate = this.registraAssegnazioneTurnoDTO.getGiorno();
        AssegnazioneTurno at = new AssegnazioneTurno(ratDate, turno);
        at.setId(id);
        this.testSchedule = this.instance.createSchedule(TODAY.getDate().plusYears(12), TODAY.getDate().plusYears(12).plusDays(5));
        Assertions.assertThrows(Exception.class, () -> this.instance.aggiungiAssegnazioneTurno(registraAssegnazioneTurnoDTO, true));

        // this should not produce any effect into the db, since id management is handled by Spring/Hibernate
        this.assegnazioneTurnoDao.saveAndFlush(at);
        Assert.assertEquals(Optional.empty(), this.assegnazioneTurnoDao.findById(id));

        // ... hence, the removal should fail (=> false is returned)
        boolean ret = this.instance.rimuoviAssegnazioneTurno(id);
        Assert.assertFalse(ret);
    }*/

    /**
     * (Domain partitioning/BVA) - Id management is totally handled by Spring/Hibernate, so it is
     * not (theoretically) possible to persist AssegnazioneTurno records with some "strange" Ids programmatically.
     * Hence, here we can only try to do it anyways, and check that Hibernate correctly reacts
     * to malformed requests.
     *
     * @param id
     */
    /*@ParameterizedTest
    @NullSource
    public void removeAssegnazioneTurnoExternalExceptionsTest(Long id) {
        Assertions.assertThrows(Exception.class, () -> this.assegnazioneTurnoDao.findById(id));
        Turno turno = this.turnoDao.findAll().get(0);
        LocalDate ratDate;
        ratDate = this.registraAssegnazioneTurnoDTO.getGiorno();
        AssegnazioneTurno at = new AssegnazioneTurno(ratDate, turno);
        at.setId(id);
        this.testSchedule = this.instance.createSchedule(TODAY.getDate().plusYears(12), TODAY.getDate().plusYears(12).plusDays(5));

        Assertions.assertThrows(Exception.class, () -> this.instance.aggiungiAssegnazioneTurno(registraAssegnazioneTurnoDTO, true));

        // this should not produce any effect into the db, since id management is handled by Spring/Hibernate
        this.assegnazioneTurnoDao.saveAndFlush(at);

        // ... hence, the removal should fail (=> false is returned)
        Assertions.assertThrows(Exception.class, () -> this.instance.rimuoviAssegnazioneTurno(id));
    }*/
}
