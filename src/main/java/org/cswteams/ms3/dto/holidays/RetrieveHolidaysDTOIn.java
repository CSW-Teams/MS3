package org.cswteams.ms3.dto.holidays;

import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@AllArgsConstructor
@Getter
public class RetrieveHolidaysDTOIn {

    @NotNull
    private final Integer year ;

    @NotNull
    @NotEmpty
    private final String country ;
}
