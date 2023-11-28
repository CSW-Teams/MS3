package org.cswteams.ms3.control.controllerscheduler;

import org.cswteams.ms3.control.servizi.ControllerServizi;
import org.cswteams.ms3.control.turni.ControllerTurni;
import org.cswteams.ms3.control.utils.MappaTurni;
import org.cswteams.ms3.control.utils.MappaUtenti;
import org.cswteams.ms3.dao.AssegnazioneTurnoDao;
import org.cswteams.ms3.dao.ScheduleDao;
import org.cswteams.ms3.dto.ModificaAssegnazioneTurnoDTO;
import org.cswteams.ms3.dto.RegistraAssegnazioneTurnoDTO;
import org.cswteams.ms3.dto.ServizioDTO;
import org.cswteams.ms3.dto.TurnoDTO;
import org.cswteams.ms3.entity.*;
import org.cswteams.ms3.enums.MansioneEnum;
import org.cswteams.ms3.enums.RuoloEnum;
import org.cswteams.ms3.enums.TipoCategoriaEnum;
import org.cswteams.ms3.enums.TipologiaTurno;
import org.cswteams.ms3.exception.AssegnazioneTurnoException;
import org.cswteams.ms3.exception.TurnoException;
import org.cswteams.ms3.control.controllerscheduler.utils.ControllerSchedulerTests;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Stream;

import static org.cswteams.ms3.control.controllerscheduler.utils.ControllerSchedulerTests.TestDatesEnum.TODAY;


@RunWith(SpringJUnit4ClassRunner.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Profile("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class AssegnazioneTurnoTests extends ControllerSchedulerTests {
    @Autowired
    AssegnazioneTurnoDao assegnazioneTurnoDao;

    @Autowired
    private ControllerServizi cs;

    @Autowired
    private ControllerTurni ct;

    @Autowired
    private ScheduleDao scheduleDAO;

    private RegistraAssegnazioneTurnoDTO registraAssegnazioneTurnoDTO;
    private Schedule testSchedule;
    private List<Utente> uGuardiaList;
    private List<Utente> uReperibiliList;

    @Before
    public void envSetup() {

        Utente uGuardia = utenteDao.findByEmailAndPassword("glrss@gmail.com", "");
        uGuardiaList = new ArrayList<>();
        uGuardiaList.add(uGuardia);

        Utente uReperibili = utenteDao.findByEmailAndPassword("salvatimartina97@gmail.com", "");
        uReperibiliList = new ArrayList<>();
        uReperibiliList.add(uReperibili);

        ServizioDTO servizioDTO1 = new ServizioDTO("ServizioTest", List.of(MansioneEnum.REPARTO));
        cs.creaServizio(servizioDTO1);

        TurnoDTO turnoDTO = new TurnoDTO();
        turnoDTO.setTipologiaTurno(TipologiaTurno.NOTTURNO);
        turnoDTO.setServizio(servizioDTO1);
        turnoDTO.setMansione(MansioneEnum.REPARTO);
        Set<Categoria> categorieVietate = new HashSet<>();

        Categoria c = new Categoria("TEST", TipoCategoriaEnum.STATO);

        categorieVietate.add(c);
        categorieDao.save(c);
        turnoDTO.setCategorieVietate(categorieVietate);

        RuoloNumero rn = new RuoloNumero(RuoloEnum.STRUTTURATO, 1);
        turnoDTO.setRuoliNumero(List.of(rn));

        turnoDTO.setOraInizio(LocalTime.of(22, 0));
        turnoDTO.setOraFine(LocalTime.of(23, 0));
        try {
            ct.creaTurno(turnoDTO);
        } catch (TurnoException e) {
            throw new RuntimeException(e);
        }

        this.testSchedule = this.instance.createSchedule(testDates.get(TODAY).minusDays(1), testDates.get(TODAY).plusDays(5));

        this.instance.createSchedule(testDates.get(TODAY).plusDays(20), testDates.get(TODAY).plusDays(30));
        this.instance.createSchedule(testDates.get(TODAY).plusYears(30).plusMonths(1), testDates.get(TODAY).plusYears(30).plusMonths(1).plusDays(5));
        //this.instance.createSchedule(LocalDate.of(1888, 4, 1), LocalDate.of(1888, 4, 5));

        this.registraAssegnazioneTurnoDTO = new RegistraAssegnazioneTurnoDTO();

        this.registraAssegnazioneTurnoDTO.setServizio(this.cs.leggiServizioByNome("ServizioTest"));
        this.registraAssegnazioneTurnoDTO.setUtentiDiGuardia(MappaUtenti.utentiEntitytoDTO(uGuardiaList));
        this.registraAssegnazioneTurnoDTO.setUtentiReperibili(MappaUtenti.utentiEntitytoDTO(uReperibiliList));

        this.registraAssegnazioneTurnoDTO.setTipologiaTurno(TipologiaTurno.NOTTURNO);
        this.registraAssegnazioneTurnoDTO.setMansione(MansioneEnum.REPARTO);

        registraAssegnazioneTurnoDTO.setAnno(testDates.get(TODAY).getYear());
        registraAssegnazioneTurnoDTO.setMese(testDates.get(TODAY).getMonthValue());
        registraAssegnazioneTurnoDTO.setGiorno(testDates.get(TODAY).getDayOfMonth() + 1);

        /*
        this. registraAssegnazioneTurnoDTO.setAnno(1888);
        this. registraAssegnazioneTurnoDTO.setMese(4);
        this. registraAssegnazioneTurnoDTO.setGiorno(3);
*/
/*
        this. registraAssegnazioneTurnoDTO.setAnno(2023);
        this. registraAssegnazioneTurnoDTO.setMese(4);
        this. registraAssegnazioneTurnoDTO.setGiorno(3);
*/
    }

    private boolean overlapCheck(Schedule a, Schedule b) {
        return !(b.getEndDate().isBefore(a.getStartDate()) || b.getStartDate().isAfter(a.getEndDate()));
    }

    private boolean perfectOverlapCheck(Schedule a, Schedule b) {
        return a.getStartDate().equals(b.getStartDate())
                && a.getEndDate().equals(b.getEndDate());
    }

    private boolean isSameScheduleCheck(Schedule a, Schedule b) {
        return a.getId().equals(b.getId());
    }

    private boolean overlapAllowanceCheck(Schedule a, Schedule b) {
        if (!overlapCheck(a, b)) {
            return true;
        } else {
            return perfectOverlapCheck(a, b) && isSameScheduleCheck(a, b);
        }

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
        List<Schedule> scheduleList = scheduleDAO.findAll();
        int ctr = 0;
        Schedule containingSchedule = null;
        for (Schedule s : scheduleList) {
            LocalDate ratDate;
            ratDate = LocalDate.of(this.registraAssegnazioneTurnoDTO.getAnno(),
                    this.registraAssegnazioneTurnoDTO.getMese(),
                    this.registraAssegnazioneTurnoDTO.getGiorno());
            if (s.getStartDate().isBefore(ratDate) && s.getEndDate().isAfter(ratDate)) {
                containingSchedule = s;
                // ... also, make sure that the schedule (if any) is only almost unique
                ctr++;
            }
        }
        int expectedAssegnazioniTurno = -1;

        // check containing schedule (almost-) uniqueness
        if (containingSchedule != null) {
            Assert.assertEquals(1, ctr);
            expectedAssegnazioniTurno = containingSchedule.getAssegnazioniTurno().size();
        } else {
            Assert.assertEquals(0, ctr);
            expectedAssegnazioniTurno = 0;
        }

        // now run the actual test
        Schedule schedule = null;
        try {
            schedule = this.instance.aggiungiAssegnazioneTurno(this.registraAssegnazioneTurnoDTO, true);
        } catch (
                AssegnazioneTurnoException e) {
            throw new RuntimeException(e);
        }
        Assert.assertNotNull(schedule);


        for (Schedule a : scheduleList) {
            Assert.assertTrue(overlapAllowanceCheck(a, schedule));
        }

        // check if the Turno was assigned
        boolean found = false;
        for (AssegnazioneTurno a : schedule.getAssegnazioniTurno()) {

            if (
                    registraAssegnazioneTurnoDTO.getTipologiaTurno().equals(a.getTurno().getTipologiaTurno())
                            && registraAssegnazioneTurnoDTO.getServizio().equals(MappaTurni.turnoEntityToDTO(a.getTurno()).getServizio())
                            && registraAssegnazioneTurnoDTO.getMansione().equals(MappaTurni.turnoEntityToDTO(a.getTurno()).getMansione())
                            && registraAssegnazioneTurnoDTO.getAnno() == (a.getData().getYear())
                            && registraAssegnazioneTurnoDTO.getMese() == (a.getData().getMonthValue())
                            && registraAssegnazioneTurnoDTO.getGiorno() == (a.getData().getDayOfMonth())
                //&& registraAssegnazioneTurnoDTO.getUtentiDiGuardia().contains(MappaUtenti.utentiEntitytoDTO(a.getUtentiDiGuardia()))
                //&& registraAssegnazioneTurnoDTO.getUtentiReperibili().contains(MappaUtenti.utentiEntitytoDTO(a.getUtentiReperibili()))
            ) {
                found = true;
                break;
            }

        }
        Assert.assertTrue(schedule.isIllegal() && !found);
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

        Long id = testSchedule.getAssegnazioniTurno().get(0).getId();
        long[] guardiaIdList = {uGuardiaList.get(0).getId()};
        long[] reperibiliIdList = {uReperibiliList.get(0).getId()};
        ModificaAssegnazioneTurnoDTO mat = new ModificaAssegnazioneTurnoDTO(
                id,
                reperibiliIdList,   // (inverted)
                guardiaIdList,      // (inverted)
                uGuardiaList.get(0).getId()
        );

        Schedule modifiedSchedule = this.instance.modificaAssegnazioneTurno(mat);
        Assert.assertNotNull(modifiedSchedule);

        // check if the AssegnazioneTurno still exists...
        Optional<AssegnazioneTurno> atRetrieved = assegnazioneTurnoDao.findById(mat.getIdAssegnazione());
        Assert.assertTrue(atRetrieved.isPresent());
        // ... and that its values are updated
        Assert.assertEquals(uReperibiliList, atRetrieved.get().getUtentiDiGuardia());
        Assert.assertEquals(uGuardiaList, atRetrieved.get().getUtentiReperibili());

    }

    @ParameterizedTest
    @MethodSource(value = "modificaAssegnazioneTurnoInvalidExceptionsParams")
    @NullSource
    public void updateAssegnazioneTurnoInvalidExceptionTest(ModificaAssegnazioneTurnoDTO mat) {
        Assertions.assertThrows(Exception.class, () -> {
            this.instance.modificaAssegnazioneTurno(mat);
        });
    }

    @Test
    public void removeAssegnazioneTurnoExternalValidTest() {
        Long id = this.testSchedule.getAssegnazioniTurno().get(0).getId();
        this.instance.rimuoviAssegnazioneTurno(id);
        Optional<AssegnazioneTurno> retrievedAt = assegnazioneTurnoDao.findById(id);
        Assert.assertFalse(retrievedAt.isPresent());
    }

    @ParameterizedTest
    @ValueSource(longs = (-1))
    @NullSource
    public void removeAssegnazioneTurnoExternalInvalidExceptionsTest(Long id) {
        Assertions.assertThrows(Exception.class, () -> this.instance.rimuoviAssegnazioneTurno(id));
    }
}
