package org.cswteams.ms3.rest;

import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;

import org.cswteams.ms3.control.preferenze.CalendarSetting;
import org.cswteams.ms3.control.preferenze.ICalendarServiceManager;
import org.cswteams.ms3.control.preferenze.IHolidayController;
import org.cswteams.ms3.control.utils.MappaHolidays;
import org.cswteams.ms3.dto.HolidayDTO;
import org.cswteams.ms3.entity.Holiday;
import org.cswteams.ms3.exception.CalendarServiceException;
import org.jboss.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/holidays")
public class HolidayRestEndpoint {
    private static Logger log = Logger.getLogger(HolidayRestEndpoint.class);

    @Autowired
    private IHolidayController holidayController;

    @Autowired
    private ICalendarServiceManager calendarServiceManager;
    
    
    private CalendarSetting setting;

    public HolidayRestEndpoint() {
    	this.setting = new CalendarSetting("https://date.nager.at/api/v3/PublicHolidays");
    }
    
    
    /**
     * 
     * @return all registered holidays
     */
    @RequestMapping(method = RequestMethod.GET, path = "/year={currentYear}/country={currentCountry}")
    public ResponseEntity<List<HolidayDTO>> getHolidays(@PathVariable String currentYear, @PathVariable String currentCountry){

        List<Holiday> holidays = holidayController.readHolidays();


        // Se il database non contiene nessuna festività e nessuna domenica, questa informaizoni vengono pescatae dall'api esterna
        if(holidays.size() == 0) {

            /*
            this.setting.addURLParameter("/" + currentYear);
            this.setting.addURLParameter("/" + currentCountry);
*/
            this.setting.setYear(currentYear);
            this.setting.setCountry(currentCountry);

            this.setting.getURL();

            calendarServiceManager.init(this.setting);


            try {
                holidays = calendarServiceManager.getHolidays();
            } catch (CalendarServiceException e) {
                e.printStackTrace();
            }

            holidayController.registerHoliday(holidays);
            holidayController.registerSundays(LocalDate.of(Integer.parseInt(currentYear)-1, 1, 1), 3);

            holidays = holidayController.readHolidays();
            this.setting.reset();

        }
        

        List<HolidayDTO> dtos = new LinkedList<>();
        for (Holiday holiday : holidays){
            dtos.add(MappaHolidays.holidayToDto(holiday));
        }


        return ResponseEntity.status(HttpStatus.FOUND).body(dtos);
    }

}
