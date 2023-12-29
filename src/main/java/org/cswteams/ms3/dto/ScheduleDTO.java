package org.cswteams.ms3.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ScheduleDTO {

    private String initialDate;
    private String finalDate;
    private long id;
    private boolean isIllegal;

    public ScheduleDTO(String initialDate, String finalDate, boolean isIllegal, long id) {
        this.initialDate = initialDate;
        this.finalDate = finalDate;
        this.isIllegal = isIllegal;
        this.id = id;
    }

    public ScheduleDTO() {
    }


}
