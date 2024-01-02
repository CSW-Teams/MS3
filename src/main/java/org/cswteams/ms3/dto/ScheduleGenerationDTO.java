package org.cswteams.ms3.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class ScheduleGenerationDTO {
    private int initialDay;
    private int initialMonth;
    private int initialYear;
    private int finalDay;
    private int finalMonth;
    private int finalYear;

    public LocalDate getStartDate(){
        return LocalDate.of(initialYear, initialMonth, initialDay);
    }

    public LocalDate getEndDate(){
        return LocalDate.of(finalYear, finalMonth, finalDay);
    }
}
