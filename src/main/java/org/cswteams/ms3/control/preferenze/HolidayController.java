package org.cswteams.ms3.control.preferenze;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.cswteams.ms3.dao.HolidayDAO;
import org.cswteams.ms3.dao.RecurrentHolidayDAO;
import org.cswteams.ms3.dto.HolidayDTO;
import org.cswteams.ms3.dto.holidays.CustomHolidayDTOIn;
import org.cswteams.ms3.dto.holidays.RetrieveHolidaysDTOIn;
import org.cswteams.ms3.entity.Holiday;
import org.cswteams.ms3.entity.RecurrentHoliday;
import org.cswteams.ms3.enums.HolidayCategory;
import org.cswteams.ms3.enums.ServiceDataENUM;
import org.cswteams.ms3.exception.CalendarServiceException;
import org.cswteams.ms3.jpa_constraints.validant.Validant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@Service
public class HolidayController implements IHolidayController {

    @Autowired
    private HolidayDAO holidayDao;

    @Autowired
    private RecurrentHolidayDAO recurrentHolidayDAO ;

    @Autowired
    private ICalendarServiceManager calendarServiceManager;

    /**
     * Registra le domeniche come festività per il numero di anni specificato
     * a partire dalla data specificata.
     * @param years
     */
    @Override
    public void registerSundays(LocalDate start, int years) {

        LocalDate sunday;

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
            registerHolidayPeriod(new HolidayDTO("Domenica", HolidayCategory.RELIGIOUS, sunday.toEpochDay(), sunday.toEpochDay(), ""));
        }

    }

    @Override
    public void registerHolidayPeriod(HolidayDTO holidayArgs){

        // last day of holiday cannot be before first day
        if (holidayArgs.getEndDateEpochDay() < holidayArgs.getStartDateEpochDay()) {
            throw new IllegalArgumentException(String.format("Holiday ends in date %s, which is before start date %s", LocalDate.ofEpochDay(holidayArgs.getEndDateEpochDay()), LocalDate.ofEpochDay(holidayArgs.getStartDateEpochDay())));
        }

        // stores the holiday period in the db
        holidayDao.save(new Holiday(holidayArgs.getName(), HolidayCategory.valueOf(holidayArgs.getCategory().toUpperCase()), holidayArgs.getStartDateEpochDay(), holidayArgs.getEndDateEpochDay(), holidayArgs.getLocation()));
    }

    @Override
    public List<HolidayDTO> retrieveRecurrentHolidays(int year) {
        List<RecurrentHoliday> holidays = recurrentHolidayDAO.findAll() ;
        ArrayList<Holiday> list = new ArrayList<>() ;
        ArrayList<HolidayDTO> dtos = new ArrayList<>() ;

        for (RecurrentHoliday hd : holidays) {
            list.add(hd.toHolidayOfYear(year)) ;
        }

        for(Holiday elem: list){
            HolidayDTO newHolidayDTO=new HolidayDTO(elem.getName(), elem.getCategory(), elem.getStartDateEpochDay(), elem.getEndDateEpochDay(), elem.getLocation());
            dtos.add(newHolidayDTO);
        }

        return dtos ;
    }

    @Override
    @Validant
    public List<HolidayDTO> readHolidays(@Valid RetrieveHolidaysDTOIn dto) throws CalendarServiceException {

        Integer currentYear = dto.getYear();
        String currentCountry = dto.getCountry();

        ArrayList<Holiday> holidays = new ArrayList<>(holidayDao.areThereHolidaysInYear(LocalDate.of(currentYear, 1, 1).toEpochDay(), LocalDate.of(currentYear, 12, 31).toEpochDay()));

        if(holidays.isEmpty()) {
            CalendarSettingBuilder calendarSettingBuilder = new CalendarSettingBuilder(ServiceDataENUM.DATANEAGER);
            calendarServiceManager.init(calendarSettingBuilder.create(currentYear.toString(), currentCountry));
            calendarServiceManager.getHolidays() ;
            registerSundays(LocalDate.of(currentYear, 1, 1), 0);
            holidays.addAll(holidayDao.areThereHolidaysInYear(LocalDate.of(currentYear, 1, 1).toEpochDay(), LocalDate.of(currentYear, 12, 31).toEpochDay())) ;
        }

        List<HolidayDTO> listDTOHoliday = new ArrayList<>();
        for(Holiday elem: holidays){
            HolidayDTO newHolidayDTO=new HolidayDTO(elem.getName(), elem.getCategory(), elem.getStartDateEpochDay(), elem.getEndDateEpochDay(), elem.getLocation());
            listDTOHoliday.add(newHolidayDTO);
        }
        listDTOHoliday.addAll(retrieveRecurrentHolidays(currentYear)) ;
        return listDTOHoliday;
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
    @Override
    @Transactional
    public void registerHolidays(@NotNull List<HolidayDTO> holidays){
        List<Holiday> listHolidays = new ArrayList<>();
        for(HolidayDTO elem: holidays){
            Holiday newHoliday=new Holiday(elem.getName(), HolidayCategory.valueOf(elem.getCategory().toUpperCase()), elem.getStartDateEpochDay(), elem.getEndDateEpochDay(), elem.getLocation());
            listHolidays.add(newHoliday);
        }
        holidayDao.saveAll(listHolidays);
    }

    @Override
    @Validant
    public void insertCustomHoliday(@Valid CustomHolidayDTOIn holiday) {

        if(holiday.isRecurrent()) {
            RecurrentHoliday holidayEnt = new RecurrentHoliday(holiday.getName(), HolidayCategory.valueOf(holiday.getKind().toUpperCase()),
                    holiday.getStartDay(), holiday.getStartMonth(), holiday.getEndDay(), holiday.getEndMonth(),
                    holiday.getLocation()) ;

            recurrentHolidayDAO.save(holidayEnt) ;
        } else {
            Holiday holidayEnt = new Holiday(holiday.getName(), HolidayCategory.valueOf(holiday.getKind().toUpperCase()),
                    holiday.getStartEpochDay(), holiday.getEndEpochDay(), holiday.getLocation()) ;

            holidayDao.save(holidayEnt) ;
        }
    }
}
