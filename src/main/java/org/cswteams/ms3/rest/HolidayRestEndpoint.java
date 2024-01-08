package org.cswteams.ms3.rest;

import java.time.LocalDate;
import java.util.List;

import org.cswteams.ms3.control.preferenze.*;
import org.cswteams.ms3.dto.HolidayDTO;
import org.cswteams.ms3.dto.holidays.CustomHolidayDTOIn;
import org.cswteams.ms3.enums.ServiceDataENUM;
import org.cswteams.ms3.exception.CalendarServiceException;
import org.jboss.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.ValidationException;

@RestController
@RequestMapping("/holidays")
public class HolidayRestEndpoint {
    private static final Logger log = Logger.getLogger(HolidayRestEndpoint.class);

    @Autowired
    private IHolidayController holidayController;

    @Autowired
    private ICalendarServiceManager calendarServiceManager;

    public HolidayRestEndpoint() {

     }
    /**
     * 
     * @return all registered holidays
     */
    @RequestMapping(method = RequestMethod.GET, path = "/year={currentYear}/country={currentCountry}")
    public ResponseEntity<List<HolidayDTO>> getHolidays(@PathVariable String currentYear, @PathVariable String currentCountry){
        List<HolidayDTO> holidays = holidayController.readHolidays();

        // Se il database non contiene nessuna festività e nessuna domenica, questa informazioni vengono pescate dall'api esterna
        if(holidays.size() == 0) {
            CalendarSettingBuilder calendarSettingBuilder = new CalendarSettingBuilder(ServiceDataENUM.DATANEAGER);
            calendarServiceManager.init(calendarSettingBuilder.create(currentYear, currentCountry));
            try {
                holidays = calendarServiceManager.getHolidays();
            } catch (CalendarServiceException e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(holidays);
            }

            holidayController.registerSundays(LocalDate.of(Integer.parseInt(currentYear)-1, 1, 1), 3);
            holidays = holidayController.readHolidays();
        }
        return ResponseEntity.status(HttpStatus.FOUND).body(holidays);
    }

    @RequestMapping(method = RequestMethod.POST, path = "/new-holiday")
    public ResponseEntity<?> insertCustomHoliday(@RequestBody CustomHolidayDTOIn dto) {

        if(dto != null) {
            try {
                holidayController.insertCustomHoliday(dto); ;
            } catch (ValidationException e) {
                return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE) ;
            } catch (Exception e) {
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR) ;
            }

            return new ResponseEntity<>(HttpStatus.OK) ;
        } else
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST) ;
    }

}
