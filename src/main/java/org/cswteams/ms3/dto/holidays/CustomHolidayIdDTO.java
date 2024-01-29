package org.cswteams.ms3.dto.holidays;

import lombok.Data;
import lombok.Getter;
import org.springframework.web.bind.annotation.PathVariable;

import javax.validation.constraints.NotNull;

@Getter
public class CustomHolidayIdDTO {

    @NotNull
    private final Long id ;

    @NotNull
    private final Boolean isRecurrent ;

    public CustomHolidayIdDTO(
            @PathVariable("id") Long id,
            @PathVariable("isRecurrent") Boolean isRecurrent
    ) {
        this.id = id ;
        this.isRecurrent = isRecurrent ;
    }
}
