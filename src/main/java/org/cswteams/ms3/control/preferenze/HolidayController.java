package org.cswteams.ms3.control.preferenze;

import java.time.LocalDate;
import java.util.List;

import org.cswteams.ms3.dao.HolidayDao;
import org.cswteams.ms3.dto.HolidayDTO;
import org.cswteams.ms3.entity.Holiday;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class HolidayController implements IHolidayController {
     
    @Autowired
    private HolidayDao holidayDao;

    @Override
    public void registerHolidayPeriod(HolidayDTO holidayArgs){

        // last day of holiday cannot be before first day
        if (holidayArgs.getEndDateEpochDay() < holidayArgs.getStartDateEpochDay()) {
            throw new IllegalArgumentException(String.format("Holiday ends in date %s, which is before start date %s", LocalDate.ofEpochDay(holidayArgs.getEndDateEpochDay()), LocalDate.ofEpochDay(holidayArgs.getStartDateEpochDay())));
        }

        // stores the holiday period in the db
        holidayDao.save(new Holiday(holidayArgs.getName(), holidayArgs.getCategory(), holidayArgs.getStartDateEpochDay(), holidayArgs.getEndDateEpochDay()));
    }

    @Override
    public List<Holiday> readHolidays() {
        return holidayDao.findAll();
    }

    @Override
    public void registerHolidayPeriod(HolidayDTO holidayArgs, int years) {
        
        // is the event repeating in the future or in the past?
        boolean isRepeatInFuture = years > 0;

        // Instead of creating a new dto at each call, we use the original one after saving its epochDay timestamps
        long startDateOld = holidayArgs.getStartDateEpochDay();
        long endDateOld = holidayArgs.getEndDateEpochDay();

        // Register a holiday period for each desired year (including the ones specified in start/end dates)
        for (int y = 0; y <= (isRepeatInFuture? years : -years); y = isRepeatInFuture? y + 1 : y - 1){

            holidayArgs.setStartDateEpochDay(LocalDate.ofEpochDay(startDateOld).plusYears(y).toEpochDay());
            holidayArgs.setEndDateEpochDay(LocalDate.ofEpochDay(endDateOld).plusYears(y).toEpochDay());
            registerHolidayPeriod(holidayArgs);
        }

        // restore original epochDay timestamps
        holidayArgs.setStartDateEpochDay(startDateOld);
        holidayArgs.setEndDateEpochDay(endDateOld);
        
    }


}
