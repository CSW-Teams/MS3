package org.cswteams.ms3.dto;

import java.time.LocalDate;

import lombok.Getter;
import org.cswteams.ms3.enums.HolidayCategory;

public class HolidayDTO {
    
    public HolidayDTO() {
    }
    public HolidayDTO(String name, HolidayCategory category, long startDateEpochDay, long endDateEpochDay, String location) {
        this.name = name;
        this.category = category.toString();
        this.setStartDateEpochDay(startDateEpochDay);
        this.setEndDateEpochDay(endDateEpochDay);
        this.location=location;
    }

    @Getter
    private String name;
    @Getter
    private String category;
    @Getter
    private String location;
    private LocalDate startDate;
    private LocalDate endDate;


    public long getStartDateEpochDay(){
        return startDate.toEpochDay();
    }

    public long getEndDateEpochDay(){
        return endDate.toEpochDay();
    }

    public void setStartDateEpochDay(long startDateEpochDay){
        this.startDate = LocalDate.ofEpochDay(startDateEpochDay);
    }
    public void setEndDateEpochDay(long endDateEpochDay){
        this.endDate = LocalDate.ofEpochDay(endDateEpochDay);
    }
}
