package org.cswteams.ms3.dto.condition;

import lombok.Getter;

/**
 * DTO used to pass from server to client conditions to be deleted from database
 */
@Getter
public class PermanentConditionDTO  {
    private final Long conditionID;
    private final long doctorID;
    private final String label;

    public PermanentConditionDTO(long doctorID, long conditionID, String label) {
        this.doctorID = doctorID;
        this.conditionID = conditionID;
        this.label = label;
    }
}
