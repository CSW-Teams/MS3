package org.cswteams.ms3.entity;

import lombok.Getter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

/**
 * This entity models a <i>Doctor</i>'s specialization.
 */
@Entity
@Getter
public class Specialization{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "specialization_id", nullable = false)
    private Long id;

    /**
     * A brief descriptor for the specialization.
     */
    @NotNull
    private String type;

    /**
     * Create a new specialization, with <code>type</code> as descriptor.
     *
     * @param type brief descriptor for the specialization.
     */
    public Specialization(String type){
        this.type = type;
    }

    /**
     * Default constructor needed by Lombok
     */
    protected Specialization() {

    }


}
