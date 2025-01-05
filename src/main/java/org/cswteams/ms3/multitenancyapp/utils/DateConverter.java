package org.cswteams.ms3.multitenancyapp.utils;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class DateConverter {
    public static String convertEpochToDateString(long epochTime){
        Instant instant = Instant.ofEpochMilli(epochTime);

        // TODO: Pick time zone in the frontend, not in the backend
        ZoneId zoneId = ZoneId.systemDefault(); // Use the system default time zone
        LocalDate localDate = instant.atZone(zoneId).toLocalDate();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return localDate.format(formatter);
    }
}
