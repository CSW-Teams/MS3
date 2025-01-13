package org.cswteams.ms3.control.preferenze;

import org.cswteams.ms3.dao.HolidayDAO;
import org.cswteams.ms3.dto.HolidayDTO;
import org.cswteams.ms3.entity.Holiday;
import org.cswteams.ms3.enums.HolidayCategory;
import org.junit.*;
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
import javax.validation.ConstraintViolationException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.junit.Assert.*;

@RunWith(Parameterized.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD) // The spring application context will be considered "dirty" before each test method, and will be rebuilt. It means that
@Transactional
@ActiveProfiles(value = "test")
public class TestHolidayControllerRegisterPeriod {


    @ClassRule
    public static final SpringClassRule scr = new SpringClassRule();

    @Rule
    public final SpringMethodRule smr = new SpringMethodRule();

    @Autowired
    private IHolidayController controller ;

    @Autowired
    private HolidayDAO dao ;

    private final HolidayDTO date ;
    private int year ;

    private final boolean isCorrect ;

    @Parameterized.Parameters
    public static Collection<Object[]> data() {

        Collection<Object[]> retVal = new ArrayList<>() ;

        HolidayDTO dtoNullName = new HolidayDTO(null, HolidayCategory.RELIGIOUS,
                LocalDate.of(2022, 9, 9).toEpochDay(), LocalDate.of(2022, 9, 30).toEpochDay(), null);

        HolidayDTO dtoNegativeStart = new HolidayDTO("Sagra dei carciofi", HolidayCategory.CORPORATE,
                -5, LocalDate.of(2022, 9, 30).toEpochDay(), null);

        HolidayDTO dtoNegativeEnd = new HolidayDTO("Sagra dei carciofi", HolidayCategory.CIVIL,
                LocalDate.of(2022, 9, 30).toEpochDay(), -47, null);

        HolidayDTO dtoLastBeforeFirst = new HolidayDTO("Sagra dei carciofi", HolidayCategory.CIVIL,
                LocalDate.of(2022, 10, 15).toEpochDay(), LocalDate.of(2022, 9, 30).toEpochDay(), null);

        HolidayDTO dtoNormalButLongPeriod = new HolidayDTO("Sagra dei carciofi", HolidayCategory.CIVIL,
                0, LocalDate.of(2022, 10, 15).toEpochDay(), null);

//        retVal.add(new Object[] {dtoNullName, -5, false}) ;
//        retVal.add(new Object[] {dtoNullName, 0, false}) ;
//        retVal.add(new Object[] {dtoNullName, 5, false}) ;
//
//        retVal.add(new Object[] {dtoNegativeStart, -5, false}) ;
//        retVal.add(new Object[] {dtoNegativeStart, 0, false}) ;
//        retVal.add(new Object[] {dtoNegativeStart, 5, false}) ;
//
//        retVal.add(new Object[] {dtoNegativeEnd, -5, false}) ;
//        retVal.add(new Object[] {dtoNegativeEnd, 0, false}) ;
//        retVal.add(new Object[] {dtoNegativeEnd, 5, false}) ;
//
//        retVal.add(new Object[] {dtoLastBeforeFirst, -5, false}) ;
//        retVal.add(new Object[] {dtoLastBeforeFirst, 0, false}) ;
//        retVal.add(new Object[] {dtoLastBeforeFirst, 5, false}) ;

        retVal.add(new Object[] {dtoNormalButLongPeriod, -5, true}) ;
        retVal.add(new Object[] {dtoNormalButLongPeriod, 0, true}) ;
        retVal.add(new Object[] {dtoNormalButLongPeriod, 5, true}) ;

        return retVal ;
    }

    public TestHolidayControllerRegisterPeriod(HolidayDTO date, int year, boolean isCorrect) {
        this.date = date;
        this.year = year;
        this.isCorrect = isCorrect;
    }

    @Test
    public void testDTO() {

        if (year > 2) {
            year = 2 ;
        }
        if( year < -2) {
            year = -2 ;
        }

        if(!isCorrect) {

            try {
                controller.registerHolidayPeriod(date, year) ;
                dao.flush();
            }
            catch (Exception e)
            {
                if(e.getClass() != ConstraintViolationException.class && e.getClass() != IllegalArgumentException.class)
                    fail() ;
            }
        } else
        {
            HolidayCategory cat = HolidayCategory.toCategory(date.getCategory());
            HolidayDTO copy = new HolidayDTO(date.getName(), cat, date.getStartDateEpochDay(), date.getEndDateEpochDay(), date.getLocation()) ;
            controller.registerHolidayPeriod(date, year) ;

            assertEquals(copy.getName(), date.getName());
            assertEquals(copy.getCategory(), date.getCategory());
            assertEquals(copy.getStartDateEpochDay(), date.getStartDateEpochDay());
            assertEquals(copy.getEndDateEpochDay(), date.getEndDateEpochDay());
            assertEquals(copy.getLocation(), date.getLocation());

            List<Holiday> holidays = dao.findAll() ;

            if(year == 0) assertEquals(1, holidays.size()) ;
            else assertEquals(Math.abs(year) +1, holidays.size()) ;

            for (Holiday holiday: holidays) {
                assertNotNull(holiday.getName()) ;
            }
        }
    }
}
