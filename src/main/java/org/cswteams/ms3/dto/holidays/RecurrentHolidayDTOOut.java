package org.cswteams.ms3.dto.holidays;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RecurrentHolidayDTOOut {

    private Long id ;
    private String name;
    private String category;
    private String location;
    private Integer startDay;
    private Integer endDay ;
    private Integer startMonth;
    private Integer endMonth ;
}
