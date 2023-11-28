package org.cswteams.ms3.control.controllerscheduler;

import org.cswteams.ms3.dto.ScheduloDTO;
import org.cswteams.ms3.entity.Schedule;
import org.cswteams.ms3.control.controllerscheduler.utils.ControllerSchedulerTests;
import org.junit.Assert;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Stream;

import static org.cswteams.ms3.control.controllerscheduler.utils.ControllerSchedulerTests.TestDatesEnum.*;
import static org.junit.jupiter.api.Assertions.assertNull;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Profile("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class ScheduleTests extends ControllerSchedulerTests {

    static Stream<Arguments> createScheduleValidTestParams() {
        return Stream.of(

                // A schedule in the past.
                Arguments.of((Object) new LocalDate[]{testDates.get(PREVIOUS_START), testDates.get(PREVIOUS_END)}),

                // A schedule in the future.
                Arguments.of((Object) new LocalDate[]{testDates.get(FUTURE_START), testDates.get(FUTURE_END)}),

                // A schedule from today to the next 5 days.
                Arguments.of((Object) new LocalDate[]{testDates.get(TODAY), testDates.get(TODAY).plusDays(5)}),

                // A schedule terminating today.
                Arguments.of((Object) new LocalDate[]{testDates.get(TODAY).minusDays(5), testDates.get(TODAY)})
                /* ,
                too much execution time! --> Arguments.of((Object) new LocalDate[]{previousStart,futureEnd})*/
        );
    }

    static Stream<Arguments> createScheduleInvalidTestParams() {
        return Stream.of(

                // End and start dates are inverted.
                Arguments.of((Object) new LocalDate[]{testDates.get(PREVIOUS_END), testDates.get(PREVIOUS_START)}),

                // Same date for start and end
                Arguments.of((Object) new LocalDate[]{testDates.get(PREVIOUS_START), testDates.get(PREVIOUS_START)})

        );
    }

    static Stream<Arguments> overlapCheckTestsParams() {
        return Stream.of(

                // already registered: [TODAY, TODAY+5]

                // not modified (identical to the already registered one)
                Arguments.of((Object) new LocalDate[]{testDates.get(TODAY), testDates.get(TODAY).plusDays(5)}),

                // shifted << 2 (partially overlapping - on the left side)
                Arguments.of((Object) new LocalDate[]{testDates.get(TODAY).minusDays(2), testDates.get(TODAY).minusDays(2).plusDays(5)}),

                // shifted >> 2 (partially overlapping - on the right side)
                Arguments.of((Object) new LocalDate[]{testDates.get(TODAY).plusDays(2), testDates.get(TODAY).plusDays(2).plusDays(5)}),

                // shifted <</>> 2 (totally overlapping)
                Arguments.of((Object) new LocalDate[]{testDates.get(TODAY).minusDays(2), testDates.get(TODAY).plusDays(5).plusDays(2)}),

                // shifted >>/<< 2 (internally overlapping)
                Arguments.of((Object) new LocalDate[]{testDates.get(TODAY).plusDays(2), testDates.get(TODAY).plusDays(4)}),

                // shifted <</<< 5 (overlapping on first day)
                Arguments.of((Object) new LocalDate[]{testDates.get(TODAY).minusDays(5), testDates.get(TODAY)}),

                // shifted >>/>> 5 (overlapping on last day)
                Arguments.of((Object) new LocalDate[]{testDates.get(TODAY).plusDays(5), testDates.get(TODAY).plusDays(5 + 5)})
        );
    }


    @ParameterizedTest
    @MethodSource(value = "createScheduleValidTestParams")
    public void createScheduleValidTest(LocalDate[] data) {
        Schedule schedule = this.instance.createSchedule(data[0], data[1]);
        Assert.assertNotNull(schedule);
        Assert.assertTrue(schedule.getId() > 0);
        Assert.assertEquals(data[0], schedule.getStartDate());
        Assert.assertEquals(data[1], schedule.getEndDate());
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
        this.instance.createSchedule(testDates.get(TODAY), testDates.get(TODAY).plusDays(5));
        Schedule overlapping = this.instance.createSchedule(date[0], date[1]);
        assertNull(overlapping);
    }

    @Test
    public void readScheduleTest() {
        this.instance.createSchedule(LocalDate.now(), LocalDate.now().plusDays(5));
        this.instance.createSchedule(LocalDate.now().plusDays(5), LocalDate.now().plusDays(10));

        List<ScheduloDTO> scheduleDTOList = this.instance.leggiSchedulazioni();
        Assert.assertNotNull(scheduleDTOList);
        Assert.assertFalse(scheduleDTOList.isEmpty());
        Assert.assertEquals(2, scheduleDTOList.size());
        for (ScheduloDTO schedule : scheduleDTOList) {
            Assert.assertTrue(schedule.getId() > 0);
        }
    }

    @Test
    public void readIllegalScheduleTest() {
        this.instance.createSchedule(LocalDate.now(), LocalDate.now().plusDays(5));
        Schedule schedule = this.instance.createSchedule(LocalDate.now().plusDays(5), LocalDate.now().plusDays(10));
        Schedule schedule2 = this.instance.createSchedule(LocalDate.now().plusDays(10), LocalDate.now().plusDays(15));
        schedule.setIllegal(true);
        schedule2.setIllegal(true);
        List<ScheduloDTO> scheduleDTOList = this.instance.leggiSchedulazioniIllegali();
        Assert.assertNotNull(scheduleDTOList);
        Assert.assertFalse(scheduleDTOList.isEmpty());
        Assert.assertEquals(2, scheduleDTOList.size());
        for (ScheduloDTO s : scheduleDTOList) {
            Assert.assertTrue(s.getId() > 0);
            Assert.assertTrue(s.isIllegalita());
        }
    }

    @ParameterizedTest
    @ValueSource(longs = {0, 1, Long.MAX_VALUE})
    public void removeScheduleByIdValidTest(long data) {

        Schedule mocked = this.instance.createSchedule(testDates.get(FUTURE_START), testDates.get(FUTURE_END));
        mocked.setId(data);
        boolean ret = this.instance.rimuoviSchedulo(data);
        Assert.assertTrue(ret);
    }

    @ParameterizedTest
    @ValueSource(ints = {-1})
    public void removeScheduleByIdInvalidTest(long data) {
        Schedule mocked = this.instance.createSchedule(testDates.get(FUTURE_START), testDates.get(FUTURE_END));
        mocked.setId(data);
        Assertions.assertThrows(Exception.class, () -> this.instance.rimuoviSchedulo(data));
    }

    @ParameterizedTest
    @MethodSource(value = "overlapCheckTestsParams")
    public void removeScheduleByOverlappingCheckTest(LocalDate[] data) {
        Schedule schedule = this.instance.createSchedule(data[0], data[1]);

        boolean ret = this.instance.rimuoviSchedulo(schedule.getId());
        Assert.assertEquals(!data[0].isBefore(testDates.get(TODAY)), ret);
    }
}
