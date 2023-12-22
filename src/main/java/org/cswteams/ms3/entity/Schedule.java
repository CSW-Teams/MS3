package org.cswteams.ms3.entity;

import lombok.Getter;
import org.cswteams.ms3.entity.constraint.Constraint;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

/** Rappresenta una pianificazione dei turni assegnati in un intervallo di date */
@Entity
@Getter
public class Schedule {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "schedule_id", nullable = false)
    private Long id;
    
    /** data di inizio validità della pianificazione, memorizzata come giorni da epoch */
    @NotNull
    private long startDate; // This date is in epoch format to keep track of the timezone

    /** data di fine validità della pianificazione, memorizzata come giorni da epoch */
    @NotNull
    private long endDate; // This date is in epoch format to keep track of the timezone

    @OneToMany(cascade = {CascadeType.ALL})
    @NotNull
    private List<ConcreteShift> concreteShifts;

    @ManyToMany
    @NotNull
    private List<Constraint> violatedConstraints;

    /**
     * Class representing a valid schedule
     * @param startDate Date of the beginning of the schedule
     * @param endDate Date of the ending of the schedule
     * @param concreteShifts List of shifts that compose the schedule (THis is a composition, not an aggregation)
     * @param violatedConstraints List of constraints that have been violated by the scheduler and that should be approved by the planner
     */
    public Schedule(Long startDate, Long endDate, List<ConcreteShift> concreteShifts, List<Constraint> violatedConstraints) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.concreteShifts = concreteShifts;
        this.violatedConstraints = violatedConstraints;
    }

    /**
     * Constructor needed when we want to create a schedule without any violated constraint
     * @param startDate Date of the beginning of the schedule
     * @param endDate Date of the ending of the schedule
     * @param concreteShifts List of shifts that compose the schedule (THis is a composition, not an aggregation)
     */
    public Schedule(Long startDate, Long endDate, List<ConcreteShift> concreteShifts) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.concreteShifts = concreteShifts;
        this.violatedConstraints = new ArrayList<>();
    }

    /**
     * Constructor needed for Spring @Entity annotation.
     * Is protected so that no one can call it except from Spring
     */
    protected Schedule(){

    }


}
