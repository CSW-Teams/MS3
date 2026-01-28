package org.cswteams.ms3.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "schedule_feedback")
public class ScheduleFeedback {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @NotNull
    @JoinColumn(name = "doctor_id")
    private Doctor doctor;

    @ManyToMany
    @JoinTable(
            name = "schedule_feedback_concrete_shifts",
            joinColumns = @JoinColumn(name = "schedule_feedback_id"),
            inverseJoinColumns = @JoinColumn(name = "concrete_shift_id")
    )
    private List<ConcreteShift> concreteShifts = new ArrayList<>();

    private String comment;

    private int score;

    private long timestamp;

    public ScheduleFeedback() {
    }

    public ScheduleFeedback(Doctor doctor, List<ConcreteShift> concreteShifts, String comment, int score, long timestamp) {
        this.doctor = doctor;
        this.concreteShifts = concreteShifts;
        this.comment = comment;
        this.score = score;
        this.timestamp = timestamp;
    }
}
