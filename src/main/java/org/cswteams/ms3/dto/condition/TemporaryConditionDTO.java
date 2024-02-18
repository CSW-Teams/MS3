package org.cswteams.ms3.dto.condition;

import lombok.Getter;

@Getter
public class TemporaryConditionDTO {
    private final Long doctorID;
    private final long conditionID;
    private final String condition;
    private final long startDate;
    private final long endDate;

    public TemporaryConditionDTO(long doctorID, long conditionID,String condition, long startDate, long endDate) {
        this.doctorID = doctorID;
        this.conditionID = conditionID;
        this.condition = condition;
        this.startDate = startDate;
        this.endDate = endDate;
    }
}
