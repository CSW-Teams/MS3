package org.cswteams.ms3.control.preferenze;

import org.cswteams.ms3.control.preferenze.IHolidayController;
import org.cswteams.ms3.dao.HolidayDao;
import org.cswteams.ms3.entity.Holiday;
import org.cswteams.ms3.enums.HolidayCategory;
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

import javax.transaction.Transactional;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(Parameterized.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD) // The spring application context will be considered "dirty" before each test method, and will be rebuilt. It means that
@Transactional
@ActiveProfiles(value = "test")
public class TestHolidayControllerSundays {

    @ClassRule
    public static final SpringClassRule scr = new SpringClassRule();

    @Rule
    public final SpringMethodRule smr = new SpringMethodRule();

    @Autowired
    private IHolidayController controller ;

    @Autowired
    private HolidayDao dao ;

    private LocalDate date ;
    private int year ;

    @Parameterized.Parameters
    public static Collection<Object[]> data() {

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
        ) ;

        Collection<Object[]> retVal = new ArrayList<>() ;

        for (LocalDate date : days) {
            retVal.add(new Object[] { date, 0 }) ;
            int inverse_secular_year = date.getYear() -1 ;
            int inverse_year = date.getYear() -1 ;
            int secular_year = date.getYear() +1 ;
            int year = date.getYear() +1 ;

            while ((year % 100 == 0 || year % 4 != 0 )) {
                year++ ;
            }

            retVal.add(new Object[] { date, year - date.getYear() }) ;

            retVal.add(new Object[] { date, year - date.getYear() +1}) ;

            while (secular_year % 400 != 0) {
                secular_year++ ;
            }

            retVal.add(new Object[] { date, secular_year - date.getYear() }) ;

            while ((inverse_year % 100 == 0 || inverse_year % 4 != 0 )) {
                inverse_year-- ;
            }

            retVal.add(new Object[] { date, inverse_year - date.getYear() }) ;

            retVal.add(new Object[] { date, inverse_year - date.getYear() -1}) ;

            while (inverse_secular_year % 400 != 0) {
                inverse_secular_year-- ;
            }

            retVal.add(new Object[] { date, inverse_secular_year - date.getYear() }) ;
        }

        return retVal ;
    }

    public TestHolidayControllerSundays(LocalDate date, int year) {
        this.date = date;
        this.year = year;
    }

    @Test
    public void testSundaysInsertion() {

        if (year > 2) {
            year = 2 ;
        }
        if( year < -2) {
            year = -2 ;
        }

        controller.registerSundays(date, year) ;

        List<Holiday> holidays = controller.readHolidays() ;

        LocalDate postPeriod ;

        LocalDate now ;

        int size = 0 ;

        if (year >= 0) {
            now = date ;
            postPeriod = LocalDate.of(date.getYear() + year, 12, 31) ;
            while (now.isBefore(postPeriod) || now.isEqual(postPeriod)) {

                if(now.getDayOfWeek() == DayOfWeek.SUNDAY) {

                    boolean contained = false ;
                    Holiday comparated = new Holiday("Domenica", HolidayCategory.RELIGIOSA, now.toEpochDay(), now.toEpochDay(), null) ;

                    for (Holiday holiday : holidays)
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
                    Holiday comparated = new Holiday("Domenica", HolidayCategory.RELIGIOSA, now.toEpochDay(), now.toEpochDay(), null) ;

                    for (Holiday holiday : holidays)
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
    }

    @After
    public void deleteHolydays() {
        dao.deleteAll() ;
    }
}
