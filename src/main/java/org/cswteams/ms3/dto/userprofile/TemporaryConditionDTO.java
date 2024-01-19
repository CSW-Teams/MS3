package org.cswteams.ms3.dto.userprofile;

import lombok.Getter;

@Getter
public class TemporaryConditionDTO {
    private final String label;
    private final long startDate;
    private final long endDate;

    /**
     * DTO used to nest attributes in the generated JSOn to be parsed by frontend in SingleUserProfileDTO
     * @param label The name of the condition
     * @param startDate The start date of the condition
     * @param endDate The end date of the condition
     */
    public TemporaryConditionDTO(String label, long startDate, long endDate){
        this.label = label;
        this.startDate = startDate;
        this.endDate = endDate;
    }
}
