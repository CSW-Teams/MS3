package org.cswteams.ms3.dto.condition;

import lombok.Getter;



/**
 * DTO used from server to client to send new conditions for a doctor
 */
@Getter
public class UpdateConditionsDTO {
    private final Long doctorID;
    private final GenericCondition condition;

    @Getter
    public static class GenericCondition{
        private final String condition;
        private final Long startDate;
        private final Long endDate;

        /**
         *
         * @param condition String representing the condition
         * @param startDate Long representing start date or 0 if it's a permanent condition
         * @param endDate Long representing end date or 0 if it's a permanent condition
         */
        public GenericCondition(String condition, Long startDate, Long endDate) {
            this.condition = condition;
            this.startDate = startDate;
            this.endDate = endDate;
        }
    }

    /**
     *
     * @param doctorID Id of the doctor we want to update
     * @param condition Set of conditions we want to update our doctor state with
     */
    public UpdateConditionsDTO(Long doctorID, GenericCondition condition) {
        this.doctorID = doctorID;
        this.condition = condition;
    }
}
