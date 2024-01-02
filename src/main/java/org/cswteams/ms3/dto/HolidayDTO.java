package org.cswteams.ms3.dto;

import java.time.LocalDate;

import lombok.Getter;
import org.cswteams.ms3.enums.HolidayCategory;

import lombok.Data;

@Data
public class HolidayDTO {
    
    public HolidayDTO() {
    }
    public HolidayDTO(String name, HolidayCategory category, long startDateEpochDay, long endDateEpochDay, String location) {
        this.name = name;
        this.category = category;
        this.setStartDateEpochDay(startDateEpochDay);
        this.setEndDateEpochDay(endDateEpochDay);
        this.setLocation(location);
    }

    @Getter
    private String name;
    @Getter
    private HolidayCategory category;
    private LocalDate startDate;
    private LocalDate endDate;
    private String Location;


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
