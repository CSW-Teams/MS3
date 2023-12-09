package org.cswteams.ms3.entity.category;

import lombok.Getter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Getter
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public class Rotation implements Category{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "rotation_id", nullable = false)
    private Long id;

    @NotNull
    private String type;

    public Rotation(String type){
        this.type = type;
    }

    protected Rotation() {

    }

    @Override
    public String getType() {
        return this.type;
    }
}
