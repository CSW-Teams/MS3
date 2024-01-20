package org.cswteams.ms3.entity.condition;

import lombok.Getter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Getter
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public abstract class Condition {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private String type;

    public Condition(String type){
        this.type = type;
    }

    protected Condition() {

    }

}

