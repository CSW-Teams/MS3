package org.cswteams.ms3.dto.holidays;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Getter
public class CustomHolidaysDTOOut {

    private List<NormalCustomHolidayDTOOut> normalHolidays ;
    private List<RecurrentHolidayDTOOut> recurrentHolidays ;
}
