package org.cswteams.ms3.rest;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.cswteams.ms3.control.preferenze.*;
import org.cswteams.ms3.dto.HolidayDTO;
import org.cswteams.ms3.dto.holidays.CustomHolidayDTOIn;
import org.cswteams.ms3.dto.holidays.RetrieveHolidaysDTOIn;
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

    public HolidayRestEndpoint() {

     }
    /**
     * 
     * @return all registered holidays
     */
    @RequestMapping(method = RequestMethod.GET, path = "/year={currentYear}/country={currentCountry}")
    public ResponseEntity<List<HolidayDTO>> getHolidays(@PathVariable Integer currentYear, @PathVariable String currentCountry){

        try {
            List<HolidayDTO> holidays = holidayController.readHolidays(new RetrieveHolidaysDTOIn(currentYear, currentCountry));
            return ResponseEntity.status(HttpStatus.FOUND).body(holidays);
        } catch (ValidationException e) {
            return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE) ;
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST) ;
        }

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
