package org.cswteams.ms3.entity;

import lombok.Getter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Getter
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public class Specialization{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "specialization_id", nullable = false)
    private Long id;
    @NotNull
    private String type;

    public Specialization(String type){
        this.type = type;
    }

    protected Specialization() {

    }


}
