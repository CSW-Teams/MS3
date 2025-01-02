package org.cswteams.ms3.control.controllerscheduler;

import org.cswteams.ms3.control.scheduler.SchedulerController;
import org.cswteams.ms3.dao.ScheduleDAO;
import org.cswteams.ms3.dto.ScheduleDTO;
import org.cswteams.ms3.entity.Schedule;
import org.cswteams.ms3.exception.IllegalAssegnazioneTurnoException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.cswteams.ms3.control.controllerscheduler.utils.TestDatesEnum.*;
import static org.junit.jupiter.api.Assertions.assertNull;

//@RunWith(SpringJUnit4ClassRunner.class)
//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//@AutoConfigureMockMvc
//@Profile("test")
//@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@SpringBootTest
public class ScheduleTests extends AbstractTransactionalJUnit4SpringContextTests {
    @Autowired
    SchedulerController instance;

    @Autowired
    ScheduleDAO scheduleDao;

    static Stream<Arguments> createScheduleValidTestParams() {
        return Stream.of(

                // A schedule in the future.
                Arguments.of((Object) new LocalDate[]{FUTURE_START.getDate(), FUTURE_END.getDate()}),

                // A schedule from today to the next 5 days.
                Arguments.of((Object) new LocalDate[]{TODAY.getDate(), TODAY.getDate().plusDays(5)})
        );
    }

    static Stream<Arguments> createScheduleInvalidTestParams() {
        return Stream.of(

                // A schedule in the past.
                Arguments.of((Object) new LocalDate[]{PREVIOUS_START.getDate(), PREVIOUS_END.getDate()}),

                // A schedule terminating today.
                Arguments.of((Object) new LocalDate[]{TODAY.getDate().minusDays(5), TODAY.getDate()}),

                // End and start dates are inverted.
                Arguments.of((Object) new LocalDate[]{PREVIOUS_END.getDate(), PREVIOUS_START.getDate()}),

                // Same date for start and end
                Arguments.of((Object) new LocalDate[]{PREVIOUS_START.getDate(), PREVIOUS_START.getDate()})
        );
    }

    static Stream<Arguments> overlapCheckTestsParams() {
        return Stream.of(

                // already registered: [TODAY.getDate(), TODAY.getDate()+5]

                // not modified (identical to the already registered one)
                Arguments.of((Object) new LocalDate[]{TODAY.getDate(), TODAY.getDate().plusDays(5)}),

                // shifted << 2 (partially overlapping - on the left side)
                Arguments.of((Object) new LocalDate[]{TODAY.getDate().minusDays(2), TODAY.getDate().minusDays(2).plusDays(5)}),

                // shifted >> 2 (partially overlapping - on the right side)
                Arguments.of((Object) new LocalDate[]{TODAY.getDate().plusDays(2), TODAY.getDate().plusDays(2).plusDays(5)}),

                // shifted <</>> 2 (totally overlapping)
                Arguments.of((Object) new LocalDate[]{TODAY.getDate().minusDays(2), TODAY.getDate().plusDays(5).plusDays(2)}),

                // shifted >>/<< 2 (internally overlapping)
                Arguments.of((Object) new LocalDate[]{TODAY.getDate().plusDays(2), TODAY.getDate().plusDays(4)}),

                // shifted <</<< 5 (overlapping on first day)
                Arguments.of((Object) new LocalDate[]{TODAY.getDate().minusDays(5), TODAY.getDate()}),

                // shifted >>/>> 5 (overlapping on last day)
                Arguments.of((Object) new LocalDate[]{TODAY.getDate().plusDays(5), TODAY.getDate().plusDays(5 + 5)})
        );
    }

    @ParameterizedTest
    @MethodSource(value = "createScheduleValidTestParams")
    public void createScheduleValidTest(LocalDate[] data) {
        Schedule schedule = this.instance.createSchedule(data[0], data[1]);
        Assert.assertNotNull(schedule);
        Assert.assertTrue(schedule.getId() > 0);
        Assert.assertEquals(data[0].toEpochDay(), schedule.getStartDate());
        Assert.assertEquals(data[1].toEpochDay(), schedule.getEndDate());
    }

    @ParameterizedTest
    @MethodSource(value = "createScheduleInvalidTestParams")
    public void createScheduleInvalidTest(LocalDate[] data) {
        Schedule schedule = this.instance.createSchedule(data[0], data[1]);
        Assert.assertNull(schedule);
    }

    @ParameterizedTest
    @NullSource
    public void createScheduleExceptionsTest(LocalDate[] data) {
        Assertions.assertThrows(Exception.class, () -> this.instance.createSchedule(data[0], data[1]));
    }

    @ParameterizedTest
    @MethodSource(value = "overlapCheckTestsParams")
    public void createOverlappingSchedulesTest(LocalDate[] date) {
        this.instance.createSchedule(TODAY.getDate(), TODAY.getDate().plusDays(5));
        Schedule overlapping = this.instance.createSchedule(date[0], date[1]);
        assertNull(overlapping);
    }

    @Test
    public void readScheduleTest() {
        this.instance.createSchedule(TODAY.getDate(), TODAY.getDate().plusDays(5));
        this.instance.createSchedule(TODAY.getDate().plusDays(6), TODAY.getDate().plusDays(11));

        List<ScheduleDTO> scheduleDTOList = this.instance.readSchedules();
        Assert.assertNotNull(scheduleDTOList);
        Assert.assertFalse(scheduleDTOList.isEmpty());
        Assert.assertEquals(2, scheduleDTOList.size());
        for (ScheduleDTO schedule : scheduleDTOList) {
            Assert.assertTrue(schedule.getId() > 0);
        }
    }

    @Test
    public void readIllegalScheduleTest() {
        this.instance.createSchedule(LocalDate.now(), LocalDate.now().plusDays(5));
        Schedule schedule = this.instance.createSchedule(FUTURE_START.getDate().plusDays(5), FUTURE_END.getDate().plusDays(10));
        Schedule schedule2 = this.instance.createSchedule(FUTURE_START.getDate().plusYears(3).plusDays(11), FUTURE_END.getDate().plusYears(3).plusDays(16));
        schedule.setCauseIllegal(new IllegalAssegnazioneTurnoException(""));
        schedule2.setCauseIllegal(new IllegalAssegnazioneTurnoException(""));
        List<ScheduleDTO> scheduleDTOList = this.instance.readSchedules();
        Assert.assertNotNull(scheduleDTOList);
        Assert.assertFalse(scheduleDTOList.isEmpty());
        Assert.assertEquals(3, scheduleDTOList.size());
        for (ScheduleDTO s : scheduleDTOList) {
            Assert.assertTrue(s.getId() > 0);
            Assert.assertTrue(s.isIllegal());
        }
    }

    @Test
    public void removeScheduleByIdValidTest() {
        Schedule mocked = this.instance.createSchedule(FUTURE_START.getDate(), FUTURE_END.getDate());
        Assert.assertNotEquals(Optional.empty(), this.scheduleDao.findById(mocked.getId()));
        Assert.assertNotNull(this.scheduleDao.findById(mocked.getId()));
        boolean ret = this.instance.removeSchedule(mocked.getId());
        Assert.assertTrue(ret);
    }
}

/**
     * (Domain partitioning/BVA) - Id management is totally handled by Spring/Hibernate, so it is
     * not (theoretically) possible to persist Schedule records with some "strange" Ids programmatically.
     * Hence, here we can only try to do it anyways, and check that Hibernate correctly reacts
     * to malformed requests.
     *
     * @param data
     
    @ParameterizedTest
    @ValueSource(longs = {0, Long.MAX_VALUE})
    public void removeScheduleByIdBoundaryValidTest(long data) {
        Assert.assertEquals(Optional.empty(), this.scheduleDao.findById(data));
        Schedule mocked = this.instance.createSchedule(FUTURE_START.getDate(), FUTURE_END.getDate());
        mocked.setId(data);

        // this should not produce any effect into the db, since id management is handled by Spring/Hibernate
        Assertions.assertThrows(Exception.class, () -> this.scheduleDao.saveAndFlush(mocked));
        Assert.assertEquals(Optional.empty(), this.scheduleDao.findById(data));
        Assertions.assertThrows(Exception.class, () -> this.instance.leggiSchedulazioni());

        // ... hence, the removal should fail (=> false is returned)
        boolean ret = this.instance.rimuoviSchedulo(data);
        Assert.assertFalse(ret);
    }

    /**
     * (Domain partitioning/BVA) - Id management is totally handled by Spring/Hibernate, so it is
     * not (theoretically) possible to persist Schedule records with some "strange" Ids programmatically.
     * Hence, here we can only try to do it anyways, and check that Hibernate correctly reacts
     * to malformed requests.
     *
     * @param data
     
    @ParameterizedTest
    @ValueSource(ints = {-1})
    public void removeScheduleByIdInvalidTest(long data) {
        Assert.assertEquals(Optional.empty(), this.scheduleDao.findById(data));
        Schedule mocked = this.instance.createSchedule(FUTURE_START.getDate(), FUTURE_END.getDate());
        mocked.setId(data);

        // this should not produce any effect into the db, since id management is handled by Spring/Hibernate
        Assertions.assertThrows(Exception.class, () -> this.scheduleDao.saveAndFlush(mocked));
        Assert.assertEquals(Optional.empty(), this.scheduleDao.findById(data));
        Assertions.assertThrows(Exception.class, () -> this.instance.leggiSchedulazioni());

        // ... hence, the removal should fail (=> false is returned)
        boolean ret = this.instance.rimuoviSchedulo(data);
        Assert.assertFalse(ret);
    }

    @ParameterizedTest
    @MethodSource(value = "overlapCheckTestsParams")
    public void removeScheduleByOverlappingCheckTest(LocalDate[] data) {
        Schedule schedule = this.instance.createSchedule(data[0], data[1]);
        if (data[0].isBefore(TODAY.getDate())) {
            Assertions.assertThrows(Exception.class, () -> this.instance.rimuoviSchedulo(schedule.getId()));
        }
    }

    @Test
    public void regenerateScheduleByIdValidTest() {
        Schedule mocked = this.instance.createSchedule(FUTURE_START.getDate(), FUTURE_END.getDate());
        Assert.assertNotEquals(Optional.empty(), this.scheduleDao.findById(mocked.getId()));
        Assert.assertNotNull(this.scheduleDao.findById(mocked.getId()));

        boolean ret = this.instance.rigeneraSchedule(mocked.getId());
        Assert.assertTrue(ret);

        List<Schedule> scheduleList = this.scheduleDao.findAll();
        boolean found = false;
        for (Schedule s : scheduleList) {
            if (ScheduleTestUtils.perfectOverlapCheck(s, mocked)) {
                found = true;
                break;
            }
        }
        Assert.assertTrue(found);
    }

    /**
     * (Domain partitioning/BVA) - Id management is totally handled by Spring/Hibernate, so it is
     * not (theoretically) possible to persist Schedule records with some "strange" Ids programmatically.
     * Hence, here we can only try to do it anyways, and check that Hibernate correctly reacts
     * to malformed requests.
     *
     * @param data
     
    @ParameterizedTest
    @ValueSource(longs = {0, Long.MAX_VALUE})
    public void regenerateScheduleByIdBoundaryValidTest(long data) {
        Assert.assertEquals(Optional.empty(), this.scheduleDao.findById(data));
        Schedule mocked = this.instance.createSchedule(FUTURE_START.getDate(), FUTURE_END.getDate());
        mocked.setId(data);

        // this should not produce any effect into the db, since id management is handled by Spring/Hibernate
        Assertions.assertThrows(Exception.class, () -> this.scheduleDao.saveAndFlush(mocked));
        Assert.assertEquals(Optional.empty(), this.scheduleDao.findById(data));
        Assertions.assertThrows(Exception.class, () -> this.instance.leggiSchedulazioni());

        // ... hence, the regeneration should fail (=> false is returned)
        boolean ret = this.instance.rigeneraSchedule(data);
        Assert.assertFalse(ret);

        Assertions.assertThrows(Exception.class, () -> this.scheduleDao.findAll());
    }

    /**
     * (Domain partitioning/BVA) - Id management is totally handled by Spring/Hibernate, so it is
     * not (theoretically) possible to persist Schedule records with some "strange" Ids programmatically.
     * Hence, here we can only try to do it anyways, and check that Hibernate correctly reacts
     * to malformed requests.
     *
     * @param data
     
    @ParameterizedTest
    @ValueSource(ints = {-1})
    public void regenerateScheduleByIdInvalidTest(long data) {
        Assert.assertEquals(Optional.empty(), this.scheduleDao.findById(data));
        Schedule mocked = this.instance.createSchedule(FUTURE_START.getDate(), FUTURE_END.getDate());
        mocked.setId(data);

        // this should not produce any effect into the db, since id management is handled by Spring/Hibernate
        Assertions.assertThrows(Exception.class, () -> this.scheduleDao.saveAndFlush(mocked));
        Assert.assertEquals(Optional.empty(), this.scheduleDao.findById(data));
        Assertions.assertThrows(Exception.class, () -> this.instance.leggiSchedulazioni());

        // ... hence, the regeneration should fail (=> false is returned)
        boolean ret = this.instance.rigeneraSchedule(data);
        Assert.assertFalse(ret);

        Assertions.assertThrows(Exception.class, () -> this.scheduleDao.findAll());

    }
}*/