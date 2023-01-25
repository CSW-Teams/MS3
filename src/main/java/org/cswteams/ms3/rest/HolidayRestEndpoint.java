package org.cswteams.ms3.rest;

import java.text.ParseException;
import java.util.LinkedList;
import java.util.List;

import org.cswteams.ms3.control.preferenze.CalendarSetting;
import org.cswteams.ms3.control.preferenze.ICalendarServiceManager;
import org.cswteams.ms3.control.preferenze.IHolidayController;
import org.cswteams.ms3.control.utils.MappaHolidays;
import org.cswteams.ms3.dto.HolidayDTO;
import org.cswteams.ms3.entity.Holiday;
import org.cswteams.ms3.exception.CalendarServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/holidays")
public class HolidayRestEndpoint {
    
    @Autowired
    private IHolidayController holidayController;

    @Autowired
    private ICalendarServiceManager calendarServiceManager;
    
    
    private CalendarSetting setting;

    public HolidayRestEndpoint() {
    	this.setting = new CalendarSetting("https://date.nager.at/api/v3/publicholidays");
    }
    
    
    /**
     * 
     * @return all registered holidays
     */
    @RequestMapping(method = RequestMethod.GET, path = "/year={currentYear}/country={currentCountry}")
    public ResponseEntity<List<HolidayDTO>> getHolidays(@PathVariable String currentYear, @PathVariable String currentCountry){
    	this.setting.addURLParameter("/" + currentYear);
    	this.setting.addURLParameter("/" + currentCountry);
    	
    	calendarServiceManager.init(this.setting);
    	
        List<Holiday> holidays = null;
		try {
			holidays = calendarServiceManager.getHolidays();
		} catch (CalendarServiceException e) {
			e.printStackTrace();
		}
        
		/*Se il servizio esterno da problemi, allora prenderà le festività dal servizio interno*/
		if (holidays == null) {
			holidays = holidayController.readHolidays();
		}

        List<HolidayDTO> dtos = new LinkedList<>();
        for (Holiday holiday : holidays){
            dtos.add(MappaHolidays.holidayToDto(holiday));
        }

        this.setting.reset();
        return ResponseEntity.status(HttpStatus.FOUND).body(dtos);
    }

}
