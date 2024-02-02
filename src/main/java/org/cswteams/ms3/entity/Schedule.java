package org.cswteams.ms3.entity;

import lombok.Getter;
import lombok.Setter;
import org.cswteams.ms3.entity.constraint.Constraint;
import org.cswteams.ms3.entity.scheduling.algo.DoctorUffaPriority;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

/** This class represents a shift schedule in an interval of dates */
@Entity
@Getter
@Setter
public class Schedule {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "schedule_id", nullable = false)
    private Long id;
    
    /** Start date of the shift schedule; it is stored as number of days from the start of Epoch. */
    @NotNull
    private long startDate; // This date is in epoch format to keep track of the timezone

    /** End date of the shift schedule; it is stored as number of days from the start of Epoch. */
    @NotNull
    private long endDate; // This date is in epoch format to keep track of the timezone

    /** Concrete shifts that compose the schedule. */
    @OneToMany(cascade = {CascadeType.ALL})
    @NotNull
    private List<ConcreteShift> concreteShifts;

    /** List of constraints violated by the shift schedule. */
    @ManyToMany
    @NotNull
    private List<Constraint> violatedConstraints;

    /** Reason for which the shift schedule results illegal */
    private Exception causeIllegal;

    /** List of priority levels on all the queues of the doctors after the schedule generation */
    @Transient
    private List<DoctorUffaPriority> doctorUffaPriorityList;

    /** Snapshot of all priority levels before the scheduling, to be restored if the scheduling is recreated */
    @OneToMany(cascade = {CascadeType.ALL})
    private List<DoctorUffaPriority> doctorUffaPrioritiesSnapshot;

    /**
     * Class representing a valid schedule
     * @param startDate Date of the beginning of the schedule
     * @param endDate Date of the ending of the schedule
     * @param concreteShifts List of shifts that compose the schedule (this is a composition, not an aggregation)
     * @param violatedConstraints List of constraints that have been violated by the scheduler and that should be approved by the planner
     */
    public Schedule(Long startDate, Long endDate, List<ConcreteShift> concreteShifts, List<Constraint> violatedConstraints) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.concreteShifts = concreteShifts;
        this.violatedConstraints = violatedConstraints;
        this.causeIllegal = null;
    }

    /**
     * Constructor needed when we want to create a schedule without any violated constraint
     * @param startDate Date of the beginning of the schedule
     * @param endDate Date of the ending of the schedule
     * @param concreteShifts List of shifts that compose the schedule (This is a composition, not an aggregation)
     */
    public Schedule(Long startDate, Long endDate, List<ConcreteShift> concreteShifts) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.concreteShifts = concreteShifts;
        this.violatedConstraints = new ArrayList<>();
        this.causeIllegal = null;
    }

    /**
     * Constructor needed for Spring @Entity annotation.
     * It is protected so that no one can call it (except Spring).
     */
    protected Schedule(){

    }


}
