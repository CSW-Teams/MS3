package org.cswteams.ms3.control.preferenze;

import org.cswteams.ms3.dto.HolidayDTO;
import org.cswteams.ms3.entity.Holiday;
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
        List<HolidayDTO> holidayList1 = calendarServiceManager.getHolidays();
        calendarServiceManager.init(b.create("2023","IT"));
        List<HolidayDTO> holidayList2 = calendarServiceManager.getHolidays();
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
        List<LocalDate> date=calendarServiceManager.getAllSundays(2024);
        List<LocalDate> dateEuristic = euristicSunday(2024);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        System.out.println("domenihe nel " + 2024 + ":");
        for (LocalDate sabato : dateEuristic) {
            System.out.println(sdf.format(sabato));
        }
        System.out.println(dateEuristic.size());
        Assert.assertEquals(dateEuristic.size(),date.size());
        for(int i=0;i<date.size();i++){
            Assert.assertEquals(date.get(i),dateEuristic.get(i));
        }
    }
    private static List<LocalDate> euristicSunday(int anno) {
        List<LocalDate> sundays = new ArrayList<>();
        LocalDate date = LocalDate.of(anno, Month.JANUARY, 1);

        while (date.getYear() == anno) {
            if (date.getDayOfWeek() == DayOfWeek.SUNDAY) {
                sundays.add(date);
            }
            date = date.plusDays(1);
        }
        return sundays;
    }
     */
}
