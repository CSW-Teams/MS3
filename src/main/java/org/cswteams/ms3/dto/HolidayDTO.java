package org.cswteams.ms3.dto;

import org.cswteams.ms3.enums.HolidayCategory;

import lombok.Data;

@Data
public class HolidayDTO {
    
    public HolidayDTO() {
    }
    public HolidayDTO(String name, HolidayCategory category, long startDateEpochDay, long endDateEpochDay) {
        this.name = name;
        this.category = category;
        this.startDateEpochDay = startDateEpochDay;
        this.endDateEpochDay = endDateEpochDay;
    }
    private String name;
    private HolidayCategory category;
    private long startDateEpochDay;
    private long endDateEpochDay;
}
