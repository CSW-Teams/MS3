package org.cswteams.ms3.entity.condition;

import lombok.Getter;

import javax.persistence.*;

@Entity
@Getter
public class PermanentCondition extends Condition{

    public PermanentCondition(String type) {
        super(type);
    }

    protected PermanentCondition(){}
}
