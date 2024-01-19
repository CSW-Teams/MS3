package org.cswteams.ms3.entity.condition;

import lombok.Getter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@MappedSuperclass
@Getter
public abstract class Condition {
    @NotNull
    private String type;



    public Condition(String type){
        this.type = type;
    }

    protected Condition() {

    }
}

