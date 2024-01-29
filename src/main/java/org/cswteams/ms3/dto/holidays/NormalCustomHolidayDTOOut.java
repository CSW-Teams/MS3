package org.cswteams.ms3.dto.holidays;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class NormalCustomHolidayDTOOut {

    private Long id ;
    private String name;
    private String category;
    private String location;
    private Long startDateEpochDay;
    private Long endDateEpochDay ;
}
