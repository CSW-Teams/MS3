package org.cswteams.ms3.control.preferenze;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;

import org.cswteams.ms3.dao.HolidayDao;
import org.cswteams.ms3.dto.HolidayDTO;
import org.cswteams.ms3.entity.Holiday;
import org.cswteams.ms3.enums.HolidayCategory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class HolidayController implements IHolidayController {
     
    @Autowired
    private HolidayDao holidayDao;
    
    /**
     * Registra le domeniche come festività per il numero di anni specificato
     * a partire dalla data specificata.
     * @param years
     */
    public void registerSundays(LocalDate start, int years) {

        LocalDate sunday;
        HolidayDTO sundayDTO = new HolidayDTO();

        if(years > 0)
            years = Math.min(years, 2) ;
        else if (years < 0) {
            years = Math.max(years, -2) ;
        }

        // we move the start date back in time if the repeat is in the past
        if (years < 0){
            start = start.plusYears(years);
            years = -years;
        }

        // finds first sunday starting from provided date
        for (sunday = start; sunday.getDayOfWeek() != DayOfWeek.SUNDAY; sunday = sunday.plusDays(1));

        // registers all sundays in desired years
        for (; sunday.getYear() - start.getYear() <= years; sunday = sunday.plusWeeks(1)) {
            sundayDTO.setName("Domenica");
            sundayDTO.setCategory(HolidayCategory.RELIGIOSA);
            sundayDTO.setStartDateEpochDay(sunday.toEpochDay());
            sundayDTO.setEndDateEpochDay(sunday.toEpochDay());
            registerHolidayPeriod(sundayDTO);

        }

    }

    @Override
    public void registerHolidayPeriod(HolidayDTO holidayArgs){
        // last day of holiday cannot be before first day
        if (holidayArgs.getEndDateEpochDay() < holidayArgs.getStartDateEpochDay()) {
            throw new IllegalArgumentException(String.format("Holiday ends in date %s, which is before start date %s", LocalDate.ofEpochDay(holidayArgs.getEndDateEpochDay()), LocalDate.ofEpochDay(holidayArgs.getStartDateEpochDay())));
        }
        // stores the holiday period in the db
        holidayDao.save(new Holiday(holidayArgs.getName(), holidayArgs.getCategory(), holidayArgs.getStartDateEpochDay(), holidayArgs.getEndDateEpochDay(), holidayArgs.getLocation()));
    }

    @Override
    public List<Holiday> readHolidays() {
        return holidayDao.findAll();
    }

    @Override
    public void registerHolidayPeriod(HolidayDTO holidayArgs, int years) {
        
        // Instead of creating a new dto at each call, we use the original one after saving its epochDay timestamps
        long startDateOld = holidayArgs.getStartDateEpochDay();
        long endDateOld = holidayArgs.getEndDateEpochDay();        
        
        // If the event is repeating in the past, we travel back in time
        // by the provided amount of years and set that as thet starting year.
        if (years < 0){
            holidayArgs.setStartDateEpochDay(LocalDate.ofEpochDay(startDateOld).plusYears(years).toEpochDay());
            holidayArgs.setEndDateEpochDay(LocalDate.ofEpochDay(endDateOld).plusYears(years).toEpochDay());
            years = -years;
        }

        // Register a holiday period for each desired year. If the repeat is in the past, the current year is not considered.
        for (int y = 0; y <= years; y ++){

            holidayArgs.setStartDateEpochDay(LocalDate.ofEpochDay(startDateOld).plusYears(y).toEpochDay());
            holidayArgs.setEndDateEpochDay(LocalDate.ofEpochDay(endDateOld).plusYears(y).toEpochDay());
            registerHolidayPeriod(holidayArgs);
        }

        // restore original epochDay timestamps
        holidayArgs.setStartDateEpochDay(startDateOld);
        holidayArgs.setEndDateEpochDay(endDateOld);
        
    }

    public void registerHoliday(List<Holiday> holidays){
        holidayDao.saveAll(holidays);
    }
}
