package org.cswteams.ms3.control.preferenze;

import org.cswteams.ms3.dto.HolidayDTO;
import org.cswteams.ms3.enums.ServiceDataENUM;
import org.cswteams.ms3.exception.CalendarServiceException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.transaction.Transactional;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@Profile("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD) // The spring application context will be considered "dirty" before each test method, and will be rebuilt. It means that
@Transactional
public class CalendarServiceManagerTest {

    @Autowired
    ICalendarServiceManager calendarServiceManager;

    @Test
    /**
     * Test per verificare che le festività non cambino da un annno all'altro
     */
    public void getHolidayTest() throws CalendarServiceException {

        CalendarSettingBuilder b = new CalendarSettingBuilder(ServiceDataENUM.DATANEAGER);
        calendarServiceManager.init(b.create("2024","IT"));
        List<HolidayDTO> holidayList1 = calendarServiceManager.getHolidays();
        calendarServiceManager.init(b.create("2023","IT"));
        List<HolidayDTO> holidayList2 = calendarServiceManager.getHolidays();
        Assert.assertEquals(holidayList2.size(),holidayList1.size());
        for(int i=0;i<holidayList1.size();i++){
            Assert.assertEquals(holidayList2.get(i).getName(),holidayList1.get(i).getName());
        }
    }

}
