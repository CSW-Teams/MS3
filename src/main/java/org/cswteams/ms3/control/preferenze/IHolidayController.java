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

    /** Registra un intervallo di date come un periodo festivo */
    void registerHolidayPeriod(HolidayDTO holidayArgs);

    /** Legge tutti i periodi festivi */
    List<HolidayDTO> readHolidays(RetrieveHolidaysDTOIn dto) throws CalendarServiceException;

    /** Registra un intervallo di date come un periodo festivo, e ripete la procedura
     * per il numero ulteriore di anni specificato .
     * @param years numero di anni da ripetere oltre quello specificato, nelle date di inizio e fine. >0 nel futuro,
     * <0 nel passato, 0 solo per l'anno specificato.
     * Se la ripetizione è nel passato, l'anno corrente non viene considerato.
     */
    void registerHolidayPeriod(HolidayDTO holidayArgs, int years);

    /**
     * Registra le domeniche come festività per il numero di anni specificato
     */
    void registerSundays(LocalDate start, int years);

    @Transactional
    void registerHolidays(@NotNull List<HolidayDTO> holidays);

    void insertCustomHoliday(CustomHolidayDTOIn holiday) ;

    List<HolidayDTO> retrieveRecurrentHolidays(int year) ;
}
