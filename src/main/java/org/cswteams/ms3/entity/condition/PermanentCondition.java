package org.cswteams.ms3.entity.condition;

import lombok.Getter;

import javax.persistence.*;

@Entity
@Getter
public class PermanentCondition extends Condition{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    public PermanentCondition(String type) {
        super(type);
    }

    protected PermanentCondition(){}
}
