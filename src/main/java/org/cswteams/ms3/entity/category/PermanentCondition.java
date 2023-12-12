package org.cswteams.ms3.entity.category;

import lombok.Getter;

import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;

@Entity
@Getter
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public class PermanentCondition extends Condition{
    public PermanentCondition(String type) {
        super(type);
    }

    protected PermanentCondition(){}
}
