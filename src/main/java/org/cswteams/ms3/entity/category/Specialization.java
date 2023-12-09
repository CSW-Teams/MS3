package org.cswteams.ms3.entity.category;

import lombok.Getter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Getter
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public class Specialization implements Category{
    /*TODO: Ask if it is useful to chain a specialization with a start and an end date (CategoriaUtente refactor)*/
    @Getter
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

    @Override
    public String getType() {
        return this.type;
    }

}
