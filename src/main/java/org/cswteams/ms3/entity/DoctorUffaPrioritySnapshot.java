package org.cswteams.ms3.entity;

import lombok.Getter;
import lombok.Setter;
import org.cswteams.ms3.dao.DoctorUffaPrioritySnapshotDAO;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Getter
@Setter
@Table(name = "doctor_uffa_priority_snapshot")
public class DoctorUffaPrioritySnapshot {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;

    /** Doctor which the Uffa priority refers to */
    @OneToOne
    @NotNull
    @JoinColumn(name = "doctor_ms3_tenant_user_id", referencedColumnName = "ms3_tenant_user_id")
    private Doctor doctor;

    /** Current schedule */
    @OneToOne
    @JoinColumn(name = "schedule_schedule_id", referencedColumnName = "schedule_id")
    private Schedule schedule;

    @Column(name = "long_shift_priority")
    private int longShiftPriority = 0;

    @Column(name = "night_priority")
    private int nightPriority = 0;

    @Column(name = "general_priority")
    private int generalPriority = 0;

    public DoctorUffaPrioritySnapshot() {}

    public DoctorUffaPrioritySnapshot(Doctor doctor) {this.doctor = doctor;}

}
