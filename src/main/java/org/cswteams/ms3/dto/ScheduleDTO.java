package org.cswteams.ms3.dto;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Table;

@Getter
@Setter
@Table(name = "schedule")

public class ScheduleDTO {

    private long initialDate;
    private long finalDate;
    private long id;
    private boolean isIllegal;

    public ScheduleDTO(long initialDate, long finalDate, boolean isIllegal, long id) {
        this.initialDate = initialDate;
        this.finalDate = finalDate;
        this.isIllegal = isIllegal;
        this.id = id;
    }

    public ScheduleDTO() {
    }


}
