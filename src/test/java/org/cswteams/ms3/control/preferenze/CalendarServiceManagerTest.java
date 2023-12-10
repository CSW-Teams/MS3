package org.cswteams.ms3.control.preferenze;
import org.cswteams.ms3.control.preferenze.CalendarServiceManager;
import org.cswteams.ms3.control.preferenze.CalendarSetting;
import org.cswteams.ms3.control.preferenze.ICalendarServiceManager;
import org.cswteams.ms3.entity.Holiday;
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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@Profile("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD) // The spring application context will be considered "dirty" before each test method, and will be rebuilt. It means that
@Transactional
class CalendarServiceManagerTest {

    @Autowired
    ICalendarServiceManager calendarServiceManager;

    @Test
    /**
     * Test per verificare che le festività non cambino da un annno all'altro
     */
    public void getHolidayTest() throws CalendarServiceException {
        //questo non solleva un eccezzione
        CalendarSettingBuilder b = new CalendarSettingBuilder(ServiceDataENUM.DATANEAGER);
        calendarServiceManager.init(b.create("2024","IT"));
        List<Holiday> holidayList1 = calendarServiceManager.getHolidays();
        calendarServiceManager.init(b.create("2023","IT"));
        List<Holiday> holidayList2 = calendarServiceManager.getHolidays();
        Assert.assertEquals(holidayList2.size(),holidayList1.size());
        for(int i=0;i<holidayList1.size();i++){
            Assert.assertEquals(holidayList2.get(i).getName(),holidayList1.get(i).getName());
        }
    }
    /*@Test*/
    /**
     * Test per verificare che anche negli anni bisestile
     *
    public void getSundayInYearTest() throws CalendarServiceException {
        //questo non solleva un eccezzione
        List<Date> date=calendarServiceManager.getAllSundays(2024);
        List<Date> dateEuristic= euristicSunday(2024);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        System.out.println("domenihe nel " + 2024 + ":");
        for (Date sabato : dateEuristic) {
            System.out.println(sdf.format(sabato));
        }
        System.out.println(dateEuristic.size());
        Assert.assertEquals(dateEuristic.size(),date.size());
        for(int i=0;i<date.size();i++){
            Assert.assertEquals(date.get(i),dateEuristic.get(i));
        }
    }
    private static List<Date> euristicSunday(int anno) {
        List<Date> sunday = new ArrayList<>();
        Calendar cal = Calendar.getInstance();
        cal.set(anno, Calendar.JANUARY, 1); // Imposta la data all'inizio dell'anno
        while (cal.get(Calendar.YEAR) == anno) {
            if (cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
                sunday.add(cal.getTime());
            }
            cal.add(Calendar.DAY_OF_MONTH,1); // Passa al giorno successivo
        }
        return sunday;
    }
     */
}
