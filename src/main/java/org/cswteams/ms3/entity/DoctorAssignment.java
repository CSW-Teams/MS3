package org.cswteams.ms3.entity;

import lombok.Getter;
import org.cswteams.ms3.enums.ConcreteShiftDoctorStatus;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Getter
public class DoctorAssignment {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "task_id", nullable = false)
    private Long id;

    @ManyToOne
    @NotNull
    private Doctor doctor;

    @Enumerated
    @NotNull
    private ConcreteShiftDoctorStatus concreteShiftDoctorStatus;

    @ManyToOne
    @NotNull
    private ConcreteShift concreteShift;

    @ManyToOne
    @NotNull
    private Task task;

    public DoctorAssignment(Doctor doctor, ConcreteShiftDoctorStatus concreteShiftDoctorStatus, ConcreteShift concreteShift, Task task){
        this.doctor = doctor;
        this.concreteShiftDoctorStatus = concreteShiftDoctorStatus;
        this.concreteShift = concreteShift;
        this.task = task;
    }

    protected DoctorAssignment(){

    }

}
