package org.cswteams.ms3.dto.condition;

import lombok.Getter;

@Getter
public class NewConditionIDReturnDTO {
    private final Long conditionID;

    public NewConditionIDReturnDTO(Long conditionID){
        this.conditionID = conditionID;
    }
}
