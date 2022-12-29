package org.cswteams.ms3.control.preferenze;

import java.util.List;

import org.cswteams.ms3.dto.HolidayDTO;
import org.cswteams.ms3.entity.Holiday;

public interface IHolidayController {

    /** Registra un intervallo di date come un periodo festivo */
    void registerHolidayPeriod(HolidayDTO holidayArgs);

    /** Legge tutti i periodi festivi */
    List<Holiday> readHolidays();

    /** Registra un intervallo di date come un periodo festivo, e ripete la procedura
     * per il numero ulteriore di anni specificato .
     * @param years numero di anni da ripetere oltre quello specificato, nelle date di inizio e fine. >0 nel futuro,
     * <0 nel passato, 0 solo per l'anno specificato.
     */
    void registerHolidayPeriod(HolidayDTO holidayArgs, int years);
}
