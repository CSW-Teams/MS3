package org.cswteams.ms3.dto.showscheduletoplanner;

import lombok.Getter;

@Getter
public class ShowScheduleToPlannerDTO {
    private final Long scheduleID;
    private final String startDate;
    private final String endDate;
    private final boolean hasViolatedConstraints;

    /**
     * DTO used to show to the planner the informations of the generated schedules.
     * This is passed from client to server
     * @param scheduleID ID of the schedule, needed to identify different schedules
     * @param startDate Start date of the schedule
     * @param endDate End date of the schedule
     * @param hasViolatedConstraints Boolean which tells if a schedule is valid or it has to be reviewed by the planner
     */
    public ShowScheduleToPlannerDTO(
            Long scheduleID,
            String startDate,
            String endDate,
            boolean hasViolatedConstraints
    ){
        this.scheduleID = scheduleID;
        this.startDate = startDate;
        this.endDate = endDate;
        this.hasViolatedConstraints = hasViolatedConstraints;
    }

}
