package org.cswteams.ms3.dto;

import java.time.LocalDate;

import org.cswteams.ms3.enums.HolidayCategory;

import lombok.Data;

@Data
public class HolidayDTO {
    
    public HolidayDTO() {
    }
    public HolidayDTO(String name, HolidayCategory category, long startDateEpochDay, long endDateEpochDay) {
        this.name = name;
        this.category = category;
        this.setStartDateEpochDay(startDateEpochDay);
        this.setEndDateEpochDay(endDateEpochDay);

    }
    private String name;
    private HolidayCategory category;
    private int startYear;
    private int startMonth;
    private int startDayOfMonth;
    private int endYear;
    private int endMonth;
    private int endDayOfMonth;

    public long getStartDateEpochDay(){
        return LocalDate.of(startYear, startMonth, startDayOfMonth).toEpochDay();
    }

    public long getEndDateEpochDay(){
        return LocalDate.of(endYear, endMonth, endDayOfMonth).toEpochDay();
    }

    public void setStartDateEpochDay(long startDateEpochDay){
        LocalDate startDate = LocalDate.ofEpochDay(startDateEpochDay);
        this.startYear = startDate.getYear();
        this.startMonth = startDate.getMonthValue();
        this.startDayOfMonth = startDate.getDayOfMonth();
    }

    public void setEndDateEpochDay(long endDateEpochDay){
        LocalDate endDate = LocalDate.ofEpochDay(endDateEpochDay);
        this.endYear = endDate.getYear();
        this.endMonth = endDate.getMonthValue();
        this.endDayOfMonth = endDate.getDayOfMonth();
    }
}
