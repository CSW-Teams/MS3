package org.cswteams.ms3.control.controllerscheduler;

import org.cswteams.ms3.AbstractMultiTenantIntegrationTest;
import org.cswteams.ms3.config.multitenancy.SchemasInitializer;
import org.cswteams.ms3.control.medicalService.MedicalServiceController;
import org.cswteams.ms3.control.scheduler.SchedulerController;
import org.cswteams.ms3.control.user.UserController;
import org.cswteams.ms3.dao.*;
import org.cswteams.ms3.dto.ScheduleDTO;
import org.cswteams.ms3.entity.*;
import org.cswteams.ms3.enums.Seniority;
import org.cswteams.ms3.enums.SystemActor;
import org.cswteams.ms3.enums.TaskEnum;
import org.cswteams.ms3.enums.TimeSlot;
import org.cswteams.ms3.exception.IllegalAssegnazioneTurnoException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.transaction.BeforeTransaction;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.io.IOException;
import java.sql.SQLException;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Stream;

import static org.cswteams.ms3.control.controllerscheduler.utils.TestDatesEnum.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(SchemasInitializer.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("test")
@Transactional
public class ScheduleTests extends AbstractMultiTenantIntegrationTest {

    @Autowired SchedulerController instance;
    @Autowired ScheduleDAO scheduleDao;
    @Autowired SpecializationDAO specializationDAO;
    @Autowired MedicalServiceController medicalServiceController;
    @Autowired MedicalServiceDAO medicalServiceDAO;
    @Autowired UserController userController;
    @Autowired DoctorDAO doctorDAO;
    @Autowired ShiftDAO shiftDAO;
    @Autowired TaskDAO taskDAO;
    @Autowired HolidayDAO holidayDAO;
    @Autowired DoctorUffaPriorityDAO doctorUffaPriorityDAO;
    @Autowired DoctorHolidaysDAO doctorHolidaysDAO;
    @Autowired DoctorUffaPrioritySnapshotDAO doctorUffaPrioritySnapshotDAO;
    @Autowired EntityManager entityManager;

    private boolean seeded = false;
    private List<DoctorUffaPriority> doctorUffaPrioritySeed;
    private List<DoctorUffaPrioritySnapshot> doctorUffaPrioritySnapshotSeed;

    @BeforeEach
    void setUp() {
        this.setUpTenantForTx();
        // clean state to avoid FK issues and deterministic expectations
        doctorUffaPrioritySnapshotDAO.deleteAll();
        doctorUffaPriorityDAO.deleteAll();
        doctorHolidaysDAO.deleteAll();
        scheduleDao.deleteAll();
        shiftDAO.deleteAll();
        medicalServiceDAO.deleteAll();
        // cleanup join table to avoid FK violations when deleting tasks
        entityManager.createNativeQuery("DELETE FROM medical_service_tasks").executeUpdate();
        doctorDAO.deleteAll();
        taskDAO.deleteAll();
        specializationDAO.deleteAll();
        holidayDAO.deleteAll();
        seeded = false;
        seedDataIfNeeded();
    }

    private void seedDataIfNeeded() {
        if (seeded && doctorDAO.count() > 0) {
            return;
        }

        // Specializations
        Specialization aLogia = new Specialization("ALOGIA");
        Specialization bLogia = new Specialization("BLOGIA");
        specializationDAO.save(aLogia);
        specializationDAO.save(bLogia);

        // Tasks and services
        Task ward = new Task(TaskEnum.WARD);
        taskDAO.saveAndFlush(ward);
        MedicalService repartoAlogia = medicalServiceController.createService(Collections.singletonList(ward), "ALOGIA");
        MedicalService repartoBlogia = medicalServiceController.createService(Collections.singletonList(ward), "BLOGIA");

        // Doctors
        Doctor doc1 = new Doctor("Esperto", "Alogia", "SLVMTN97T56H501Y", LocalDate.of(1997, 3, 14), "espertoalogia@gmail.com", "passw", Seniority.STRUCTURED, Set.of(SystemActor.CONFIGURATOR));
        Doctor doc2 = new Doctor("Esperto", "Blogia", "SLVMTN97T56H501Y", LocalDate.of(1997, 3, 14), "espertoblogia97@gmail.com", "passw", Seniority.SPECIALIST_SENIOR, Set.of(SystemActor.CONFIGURATOR));
        try {
            userController.addSpecialization(doc1, aLogia);
            userController.addSpecialization(doc2, bLogia);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to add specialization", e);
        }
        doctorDAO.save(doc1);
        doctorDAO.save(doc2);

        // Shifts with QSS
        Map<Seniority, Integer> alogiaQuantities = Map.of(Seniority.STRUCTURED, 1);
        QuantityShiftSeniority repartoAlogiaQss = new QuantityShiftSeniority(new HashMap<>(alogiaQuantities), ward);
        Map<Seniority, Integer> blogiaQuantities = Map.of(Seniority.SPECIALIST_SENIOR, 1);
        QuantityShiftSeniority repartoBlogiaQss = new QuantityShiftSeniority(new HashMap<>(blogiaQuantities), ward);
        Set<DayOfWeek> allDays = EnumSet.allOf(DayOfWeek.class);
        shiftDAO.saveAndFlush(new Shift(LocalTime.of(8, 0), Duration.ofHours(6), repartoAlogia, TimeSlot.MORNING, Collections.singletonList(repartoAlogiaQss), allDays, Collections.emptyList()));
        shiftDAO.saveAndFlush(new Shift(LocalTime.of(8, 0), Duration.ofHours(6), repartoBlogia, TimeSlot.MORNING, Collections.singletonList(repartoBlogiaQss), allDays, Collections.emptyList()));

        // Holidays and doctor holidays
        List<Holiday> holidays = holidayDAO.findAll();
        HashMap<Holiday, Boolean> holidayMap = new HashMap<>();
        for (Holiday h : holidays) {
            if (!"Domenica".equals(h.getName())) {
                holidayMap.put(h, false);
            }
        }
        doctorHolidaysDAO.save(new DoctorHolidays(doc1, holidayMap));
        doctorHolidaysDAO.save(new DoctorHolidays(doc2, holidayMap));

        // Seed priorities (not persisted)
        doctorUffaPrioritySeed = List.of(new DoctorUffaPriority(doc1), new DoctorUffaPriority(doc2));
        doctorUffaPrioritySnapshotSeed = List.of(new DoctorUffaPrioritySnapshot(doc1), new DoctorUffaPrioritySnapshot(doc2));
        seeded = true;
    }

    private Schedule createScheduleUsingSeed(LocalDate startDate, LocalDate endDate) {
        List<DoctorUffaPriority> dupCopy = new ArrayList<>();
        for (DoctorUffaPriority dup : doctorUffaPrioritySeed) {
            dupCopy.add(new DoctorUffaPriority(dup.getDoctor()));
        }
        List<DoctorUffaPrioritySnapshot> snapCopy = new ArrayList<>();
        for (DoctorUffaPrioritySnapshot snap : doctorUffaPrioritySnapshotSeed) {
            snapCopy.add(new DoctorUffaPrioritySnapshot(snap.getDoctor()));
        }
        return this.instance.createSchedule(startDate, endDate, dupCopy, snapCopy);
    }

    static Stream<Arguments> createScheduleValidTestParams() {
        return Stream.of(
                Arguments.of((Object) new LocalDate[]{FUTURE_START.getDate(), FUTURE_END.getDate()}),
                Arguments.of((Object) new LocalDate[]{TODAY.getDate(), TODAY.getDate().plusDays(5)})
        );
    }

    static Stream<Arguments> createScheduleInvalidTestParams() {
        return Stream.of(
                Arguments.of((Object) new LocalDate[]{PREVIOUS_START.getDate(), PREVIOUS_END.getDate()}),
                Arguments.of((Object) new LocalDate[]{TODAY.getDate().minusDays(5), TODAY.getDate()}),
                Arguments.of((Object) new LocalDate[]{PREVIOUS_END.getDate(), PREVIOUS_START.getDate()}),
                Arguments.of((Object) new LocalDate[]{PREVIOUS_START.getDate(), PREVIOUS_START.getDate()})
        );
    }

    static Stream<Arguments> overlapCheckTestsParams() {
        return Stream.of(
                Arguments.of((Object) new LocalDate[]{TODAY.getDate(), TODAY.getDate().plusDays(5)}),
                Arguments.of((Object) new LocalDate[]{TODAY.getDate().minusDays(2), TODAY.getDate().minusDays(2).plusDays(5)}),
                Arguments.of((Object) new LocalDate[]{TODAY.getDate().plusDays(2), TODAY.getDate().plusDays(2).plusDays(5)}),
                Arguments.of((Object) new LocalDate[]{TODAY.getDate().minusDays(2), TODAY.getDate().plusDays(5).plusDays(2)}),
                Arguments.of((Object) new LocalDate[]{TODAY.getDate().plusDays(2), TODAY.getDate().plusDays(4)}),
                Arguments.of((Object) new LocalDate[]{TODAY.getDate().minusDays(5), TODAY.getDate()}),
                Arguments.of((Object) new LocalDate[]{TODAY.getDate().plusDays(5), TODAY.getDate().plusDays(10)})
        );
    }

    @ParameterizedTest
    @MethodSource("createScheduleValidTestParams")
    void createScheduleValidTest(LocalDate[] data) {
        Schedule schedule = createScheduleUsingSeed(data[0], data[1]);
        assertNotNull(schedule);
        assertTrue(schedule.getId() > 0);
        assertEquals(data[0].toEpochDay(), schedule.getStartDate());
        assertEquals(data[1].toEpochDay(), schedule.getEndDate());
    }

    @ParameterizedTest
    @MethodSource("createScheduleInvalidTestParams")
    void createScheduleInvalidTest(LocalDate[] data) {
        Schedule schedule = createScheduleUsingSeed(data[0], data[1]);
        assertNull(schedule);
    }

    @ParameterizedTest
    @NullSource
    void createScheduleExceptionsTest(LocalDate[] data) {
        assertThrows(Exception.class, () -> this.instance.createSchedule(data[0], data[1], null, null));
    }

    @ParameterizedTest
    @MethodSource("overlapCheckTestsParams")
    void createOverlappingSchedulesTest(LocalDate[] date) {
        createScheduleUsingSeed(TODAY.getDate(), TODAY.getDate().plusDays(5));
        Schedule overlapping = createScheduleUsingSeed(date[0], date[1]);
        assertNotNull(overlapping);
    }

    @Test
    void readScheduleTest() {
        createScheduleUsingSeed(TODAY.getDate(), TODAY.getDate().plusDays(5));
        createScheduleUsingSeed(TODAY.getDate().plusDays(6), TODAY.getDate().plusDays(11));

        List<ScheduleDTO> scheduleDTOList = this.instance.readSchedules();
        assertNotNull(scheduleDTOList);
        assertFalse(scheduleDTOList.isEmpty());
        // con le due create sopra ci aspettiamo almeno 2 record
        assertTrue(scheduleDTOList.size() >= 2);
        for (ScheduleDTO schedule : scheduleDTOList) {
            assertTrue(schedule.getId() > 0);
        }
    }

    @Test
    void readIllegalScheduleTest() {
        createScheduleUsingSeed(LocalDate.now(), LocalDate.now().plusDays(5));
        Schedule schedule = createScheduleUsingSeed(FUTURE_START.getDate().plusDays(5), FUTURE_END.getDate().plusDays(10));
        Schedule schedule2 = createScheduleUsingSeed(FUTURE_START.getDate().plusYears(3).plusDays(11), FUTURE_END.getDate().plusYears(3).plusDays(16));
        schedule.setCauseIllegal(new IllegalAssegnazioneTurnoException(""));
        schedule2.setCauseIllegal(new IllegalAssegnazioneTurnoException(""));
        List<ScheduleDTO> scheduleDTOList = this.instance.readSchedules();
        assertNotNull(scheduleDTOList);
        assertFalse(scheduleDTOList.isEmpty());
        // dopo il seed sopra ci aspettiamo almeno 3 elementi
        assertTrue(scheduleDTOList.size() >= 3);
        for (ScheduleDTO s : scheduleDTOList) {
            assertTrue(s.getId() > 0);
            assertTrue(s.isIllegal());
        }
    }

    @Test
    void removeScheduleByIdValidTest() {
        Schedule mocked = createScheduleUsingSeed(FUTURE_START.getDate(), FUTURE_END.getDate());
        assertNotNull(mocked, "Schedule creation failed, cannot test removal");
        assertNotEquals(Optional.empty(), this.scheduleDao.findById(mocked.getId()));
        assertNotNull(this.scheduleDao.findById(mocked.getId()));
        boolean ret = this.instance.removeSchedule(mocked.getId());
        assertTrue(ret);
    }
}
