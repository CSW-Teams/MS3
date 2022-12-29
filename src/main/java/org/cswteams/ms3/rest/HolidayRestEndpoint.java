package org.cswteams.ms3.rest;

import java.util.LinkedList;
import java.util.List;

import org.cswteams.ms3.control.preferenze.IHolidayController;
import org.cswteams.ms3.control.utils.MappaHolidays;
import org.cswteams.ms3.dto.HolidayDTO;
import org.cswteams.ms3.entity.Holiday;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/holidays")
public class HolidayRestEndpoint {
    
    @Autowired
    private IHolidayController holidayController;

    /** gets all registered holidays */
    @GetMapping(path = "/")
    public ResponseEntity<List<HolidayDTO>> getHolidays(){
        
        List<HolidayDTO> dtos = new LinkedList<>();
        List<Holiday> holidays = holidayController.readHolidays();

        for (Holiday holiday : holidays){
            dtos.add(MappaHolidays.holidayToDto(holiday));
        }

        return ResponseEntity.status(HttpStatus.FOUND).body(dtos);
    }

}
