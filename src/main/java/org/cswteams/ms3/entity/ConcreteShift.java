package org.cswteams.ms3.entity;


import lombok.Getter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
public class ConcreteShift {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "concrete_shift_id", nullable = false)
    private Long id;

    @NotNull
    private long date; // This date is in epoch format to keep track of the timezone

    @ManyToOne
    @NotNull
    private Shift shift;

    
    protected ConcreteShift(Long id) {

        this.id = id;
    }

    /**
     * This class represents the concrete shift present in a schedule for a certain date.
     * This class should be operated only by the planners.
     * @param date The date of the concrete shift
     * @param shift The abstract shift from which this shift is created
     */
    public ConcreteShift(Long date, Shift shift) {
        this.date = date;
        this.shift = shift;
    }


    protected ConcreteShift() {

    }
}