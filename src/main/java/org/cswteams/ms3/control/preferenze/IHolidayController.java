package org.cswteams.ms3.control.preferenze;

import java.time.LocalDate;
import java.util.List;

import org.cswteams.ms3.dto.HolidayDTO;
import org.cswteams.ms3.dto.holidays.CustomHolidayDTOIn;
import org.cswteams.ms3.dto.holidays.RetrieveHolidaysDTOIn;
import org.cswteams.ms3.exception.CalendarServiceException;

import javax.transaction.Transactional;
import javax.validation.constraints.NotNull;

public interface IHolidayController {

    /**
     * Register a date range as a holiday
     */
    void registerHolidayPeriod(HolidayDTO holidayArgs);

    /**
     * Read all holiday periods
     */
    List<HolidayDTO> readHolidays(RetrieveHolidaysDTOIn dto) throws CalendarServiceException;

    /**
     * Registers a date range as a holiday, and repeats the procedure
     * for the additional number of years specified.
     *
     * @param years number of years to repeat beyond the specified one, in the start and end dates.
     *              greater than 0 in the future,
     *              less than 0 in the past,
     *              equal to 0 for the specified year only.
     *              If the repetition is in the past, the current year is not considered.
     */
    void registerHolidayPeriod(HolidayDTO holidayArgs, int years);

    /**
     * Records Sundays as holidays for the specified number of years, starting from <code>start</code>.
     * @param start start date
     * @param years number of years
     */
    void registerSundays(LocalDate start, int years);

    @Transactional
    void registerHolidays(@NotNull List<HolidayDTO> holidays);

    void insertCustomHoliday(CustomHolidayDTOIn holiday) ;

    List<HolidayDTO> retrieveRecurrentHolidays(int year) ;
}
