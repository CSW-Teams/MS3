package org.cswteams.ms3.control.preferenze;

import org.cswteams.ms3.dao.HolidayDAO;
import org.cswteams.ms3.dto.HolidayDTO;
import org.cswteams.ms3.entity.Holiday;
import org.cswteams.ms3.enums.HolidayCategory;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.time.LocalDate;
import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@ActiveProfiles(value = "test")
public class TestHolidayControllerMisc {
    /*

    @Autowired
    private IHolidayController controller ;

    @Autowired
    private HolidayDAO dao ;

    @Test
    public void testSundaysNullValues() {

        int exceptions = 0 ;

        try {
            controller.registerSundays(null, 0);
        } catch (NullPointerException ignored)
        {
            exceptions++ ;
        }

        try {
            controller.registerSundays(null, 100);
        } catch (NullPointerException e)
        {
            exceptions++ ;
        }

        try {
            controller.registerSundays(null, -34);
        } catch (NullPointerException e)
        {
            exceptions++ ;
        }

        assertEquals(3, exceptions) ;
    }

    @Test
    public void testSundays30December0Years() {

        controller.registerSundays(LocalDate.of(2014, 12, 30), 0) ;

        List<Holiday> holidays = dao.findAll() ;

        assertTrue(holidays.isEmpty()) ;
    }

    @Test
    public void testRegisterHolidayPeriod0YearsIndifferent() {

        HolidayDTO dto = new HolidayDTO() ;

        dto.setName("NataleSantoStefano");
        dto.setCategory(HolidayCategory.RELIGIOUS) ;
        dto.setLocation("Lapponia") ;
        dto.setStartDateEpochDay(LocalDate.of(2023, 12, 25).toEpochDay());
        dto.setEndDateEpochDay(LocalDate.of(2023, 12, 26).toEpochDay()) ;

        controller.registerHolidayPeriod(dto, 0) ;

        List<Holiday> holidays = controller.readHolidays() ;

        assertEquals(1, holidays.size());

        dao.deleteAll() ;

        controller.registerHolidayPeriod(dto) ;

        List<Holiday> holidays2 = controller.readHolidays() ;

        assertEquals(1, holidays2.size());

        Holiday holiday1, holiday2 ;

        holiday1 = holidays.get(0) ;
        holiday2 = holidays2.get(0) ;

        assertEquals(holiday1.getName(), holiday2.getName());
        assertEquals(holiday1.getCategory(), holiday2.getCategory());
        assertEquals(holiday1.getLocation(), holiday2.getLocation());
        assertEquals(holiday1.getStartDateEpochDay(), holiday2.getStartDateEpochDay());
        assertEquals(holiday1.getEndDateEpochDay(), holiday2.getEndDateEpochDay());
    }

    @After
    public void cleanUp() {
        dao.deleteAll() ;
    }
     */
}
