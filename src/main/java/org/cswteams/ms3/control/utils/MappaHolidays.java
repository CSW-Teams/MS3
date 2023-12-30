package org.cswteams.ms3.control.utils;

import java.time.LocalDate;

import org.cswteams.ms3.dto.HolidayDTO;
import org.cswteams.ms3.entity.Holiday;
import org.cswteams.ms3.enums.HolidayCategory;

public class MappaHolidays {
    
    private MappaHolidays(){
        // private constructor
    }

    public static Holiday dtoToHoliday(HolidayDTO dto){
        return new Holiday(dto.getName(), HolidayCategory.valueOf(dto.getCategory()), dto.getStartDateEpochDay(), dto.getEndDateEpochDay(), dto.getLocation());
    }

    public static HolidayDTO holidayToDto(Holiday holiday){
        return new HolidayDTO(holiday.getName(), holiday.getCategory(), holiday.getStartDateEpochDay(), holiday.getEndDateEpochDay(), holiday.getLocation());
    }

}
