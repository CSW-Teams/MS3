package org.cswteams.ms3.dto.condition;

import lombok.Getter;

import java.util.Set;

@Getter
public class AllSavedConditionDTO {
    private final Set<SingleSavedCondition> allSavedConditions;

    @Getter
    public static class SingleSavedCondition{
        private final Long conditionID;
        private final String label;
        private final Long startDate;
        private final Long endDate;
        private final boolean isPermanent;

        public SingleSavedCondition(Long conditionID, String label, Long startDate, Long endDate,boolean isPermanent) {
            this.conditionID = conditionID;
            this.label = label;
            this.startDate = startDate;
            this.endDate = endDate;
            this.isPermanent = isPermanent;
        }
    }

    public AllSavedConditionDTO(Set<SingleSavedCondition> allSavedConditions) {
        this.allSavedConditions = allSavedConditions;
    }
}
