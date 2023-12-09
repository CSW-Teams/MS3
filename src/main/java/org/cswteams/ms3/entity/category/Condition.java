package org.cswteams.ms3.entity.category;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@MappedSuperclass
public abstract class Condition implements Category{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "condition_id", nullable = false)
    private Long id;
    @NotNull
    private String type;

    public Condition(String type){
        this.type = type;
    }

    protected Condition() {

    }

    @Override
    public String getType() {
        return this.type;
    }
}
