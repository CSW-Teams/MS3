package org.cswteams.ms3.entity;

import lombok.Getter;
import lombok.Setter;
import org.cswteams.ms3.enums.ConcreteShiftDoctorStatus;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

/**
 * This entity models the association between <i>Doctors</i> and <i>concrete shifts</i>.
 */
@Entity
@Getter
@Table(name = "doctor_assignment") //see issue #413
public class DoctorAssignment {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "task_id", nullable = false)
    private Long id;

    @ManyToOne
    @NotNull
    @Setter
    @JoinColumn(name = "doctor_ms3_tenant_user_id")
    private Doctor doctor;

    @Enumerated
    @NotNull
    @Setter
    @Column(name = "concrete_shift_doctor_status")
    private ConcreteShiftDoctorStatus concreteShiftDoctorStatus;

    @ManyToOne
    @JoinColumn(name = "concrete_shift_id")
    @NotNull
    private ConcreteShift concreteShift;

    @ManyToOne
    @NotNull
    @JoinColumn(name = "task_task_id")
    private Task task;

    /**
     * Create a <i>Doctor</i>-<i>concrete shift</i> association with the specified parameters
     *
     * @param doctor                    <i>Doctor</i> involved
     * @param concreteShiftDoctorStatus state with which the <i>Doctor</i> is involved in the association
     * @param concreteShift             <i>concrete shift</i>
     * @param task                      <i>task</i> for the association
     */
    public DoctorAssignment(Doctor doctor, ConcreteShiftDoctorStatus concreteShiftDoctorStatus, ConcreteShift concreteShift, Task task) {
        this.doctor = doctor;
        this.concreteShiftDoctorStatus = concreteShiftDoctorStatus;
        this.concreteShift = concreteShift;
        this.task = task;
    }

    /**
     * Default constructor needed by Lombok
     */
    protected DoctorAssignment() {

    }

}