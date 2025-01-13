package org.cswteams.ms3.control.preferenze;

import org.cswteams.ms3.dao.HolidayDAO;
import org.cswteams.ms3.dto.HolidayDTO;
import org.cswteams.ms3.dto.holidays.RetrieveHolidaysDTOIn;
import org.cswteams.ms3.entity.Holiday;
import org.cswteams.ms3.enums.HolidayCategory;
import org.cswteams.ms3.exception.CalendarServiceException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.transaction.Transactional;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@SpringBootTest
@Transactional
public class TestHolidayControllerSundays {

    @Autowired
    private HolidayController controller ;

    @Autowired
    private HolidayDAO dao ;

//    public static Collection<Object[]> data() {
//
//        Set<LocalDate> days = Set.of(
//                LocalDate.of(2007, 2, 15),
//                LocalDate.of(2007, 2, 25),
//                LocalDate.of(2007, 4, 12),
//                LocalDate.of(2007, 4, 1),
//                LocalDate.of(2007, 7, 13),
//                LocalDate.of(2007, 7, 29),
//
//                LocalDate.of(2008, 2, 29),
//                LocalDate.of(2008, 2, 25),
//                LocalDate.of(2008, 4, 12),
//                LocalDate.of(2008, 4, 27),
//                LocalDate.of(2008, 7, 13),
//                LocalDate.of(2008, 7, 3),
//
//                LocalDate.of(2000, 2, 29),
//                LocalDate.of(2000, 2, 13),
//                LocalDate.of(2000, 4, 12),
//                LocalDate.of(2000, 4, 2),
//                LocalDate.of(2000, 7, 13),
//                LocalDate.of(2000, 7, 30)
//        ) ;
//
//        Collection<Object[]> retVal = new ArrayList<>() ;
//
//        for (LocalDate date : days) {
//            retVal.add(new Object[] { date, 0 }) ;
//            int inverse_secular_year = date.getYear() -1 ;
//            int inverse_year = date.getYear() -1 ;
//            int secular_year = date.getYear() +1 ;
//            int year = date.getYear() +1 ;
//
//            while ((year % 100 == 0 || year % 4 != 0 )) {
//                year++ ;
//            }
//
//            retVal.add(new Object[] { date, year - date.getYear() }) ;
//
//            retVal.add(new Object[] { date, year - date.getYear() +1}) ;
//
//            while (secular_year % 400 != 0) {
//                secular_year++ ;
//            }
//
//            retVal.add(new Object[] { date, secular_year - date.getYear() }) ;
//
//            while ((inverse_year % 100 == 0 || inverse_year % 4 != 0 )) {
//                inverse_year-- ;
//            }
//
//            retVal.add(new Object[] { date, inverse_year - date.getYear() }) ;
//
//            retVal.add(new Object[] { date, inverse_year - date.getYear() -1}) ;
//
//            while (inverse_secular_year % 400 != 0) {
//                inverse_secular_year-- ;
//            }
//
//            retVal.add(new Object[] { date, inverse_secular_year - date.getYear() }) ;
//        }
//
//        return retVal ;
//    }

    public static Stream<Arguments> data() {
        Set<LocalDate> days = Set.of(
                LocalDate.of(2007, 2, 15),
                LocalDate.of(2007, 2, 25),
                LocalDate.of(2007, 4, 12),
                LocalDate.of(2007, 4, 1),
                LocalDate.of(2007, 7, 13),
                LocalDate.of(2007, 7, 29),

                LocalDate.of(2008, 2, 29),
                LocalDate.of(2008, 2, 25),
                LocalDate.of(2008, 4, 12),
                LocalDate.of(2008, 4, 27),
                LocalDate.of(2008, 7, 13),
                LocalDate.of(2008, 7, 3),

                LocalDate.of(2000, 2, 29),
                LocalDate.of(2000, 2, 13),
                LocalDate.of(2000, 4, 12),
                LocalDate.of(2000, 4, 2),
                LocalDate.of(2000, 7, 13),
                LocalDate.of(2000, 7, 30)
        );

        return days.stream().flatMap(date -> {
            Stream.Builder<Arguments> builder = Stream.builder();

            builder.add(Arguments.of(date, 0));

            int inverse_secular_year = date.getYear() - 1;
            int inverse_year = date.getYear() - 1;
            int secular_year = date.getYear() + 1;
            int year = date.getYear() + 1;

            while ((year % 100 == 0 || year % 4 != 0)) {
                year++;
            }

            builder.add(Arguments.of(date, year - date.getYear()));
            builder.add(Arguments.of(date, year - date.getYear() + 1));

            while (secular_year % 400 != 0) {
                secular_year++;
            }

            builder.add(Arguments.of(date, secular_year - date.getYear()));

            while ((inverse_year % 100 == 0 || inverse_year % 4 != 0)) {
                inverse_year--;
            }

            builder.add(Arguments.of(date, inverse_year - date.getYear()));
            builder.add(Arguments.of(date, inverse_year - date.getYear() - 1));

            while (inverse_secular_year % 400 != 0) {
                inverse_secular_year--;
            }

            builder.add(Arguments.of(date, inverse_secular_year - date.getYear()));

            return builder.build();
        });
    }

    /** todo: there is an error following the code like this:
     * TestHolidayControllerSundays:    182:    controller.readHolidays(retrieveHolidaysDTOIn)  ->
     * HolidayController:               148:    calendarServiceManager.getHolidays();           ->
     * CalendarServiceManager:          51:     (JSONArray) JSONValue.parse(response.body());
     *
     * the response body is not a JSon array so it returns an exception
     */
    @ParameterizedTest
    @Disabled
    @MethodSource("data")
    public void testSundaysInsertion(LocalDate date, int year) {

        if (year > 2) {
            year = 2 ;
        }
        if( year < -2) {
            year = -2 ;
        }

        try {
            controller.registerSundays(date, year) ;

            RetrieveHolidaysDTOIn retrieveHolidaysDTOIn = new RetrieveHolidaysDTOIn(2023, "Lapponia") ;

            List<HolidayDTO> holidays = controller.readHolidays(retrieveHolidaysDTOIn);

            LocalDate postPeriod ;

            LocalDate now ;

            int size = 0 ;

            if (year >= 0) {
                now = date ;
                postPeriod = LocalDate.of(date.getYear() + year, 12, 31) ;
                while (now.isBefore(postPeriod) || now.isEqual(postPeriod)) {

                    if(now.getDayOfWeek() == DayOfWeek.SUNDAY) {

                        boolean contained = false ;
                        Holiday comparated = new Holiday("Domenica", HolidayCategory.RELIGIOUS, now.toEpochDay(), now.toEpochDay(), null) ;

                        for (HolidayDTO holiday : holidays)
                        {
                            if(holiday.getName().equals(comparated.getName()) &&
                                holiday.getCategory().equals(comparated.getCategory()) &&
                                holiday.getStartDateEpochDay() == comparated.getStartDateEpochDay() &&
                                holiday.getEndDateEpochDay() == comparated.getEndDateEpochDay()) {

                                contained = true ;
                                size++ ;

                            }
                        }
                        assertTrue(contained) ;
                    }

                    now = now.plusDays(1) ;
                }
            } else {
                now = LocalDate.of(date.getYear(), 12, 31) ;
                postPeriod = date.plusYears(year) ;
                while (now.isAfter(postPeriod) || now.isEqual(postPeriod)) {

                    if(now.getDayOfWeek() == DayOfWeek.SUNDAY) {

                        boolean contained = false ;
                        Holiday comparated = new Holiday("Domenica", HolidayCategory.RELIGIOUS, now.toEpochDay(), now.toEpochDay(), null) ;

                        for (HolidayDTO holiday : holidays)
                        {
                            if(holiday.getName().equals(comparated.getName()) &&
                                    holiday.getCategory().equals(comparated.getCategory()) &&
                                    holiday.getStartDateEpochDay() == comparated.getStartDateEpochDay() &&
                                    holiday.getEndDateEpochDay() == comparated.getEndDateEpochDay()) {

                                contained = true ;
                                size++ ;

                            }
                        }
                        assertTrue(contained) ;
                    }

                    now = now.minusDays(1) ;
                }
            }

            assertEquals(holidays.size(), size);

        } catch (CalendarServiceException e) {
            throw new RuntimeException(e);
        }
    }

    @AfterEach
    public void deleteHolydays() {
        dao.deleteAll() ;
    }
}
