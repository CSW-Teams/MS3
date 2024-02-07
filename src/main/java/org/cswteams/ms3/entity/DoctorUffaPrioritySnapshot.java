package org.cswteams.ms3.entity;

import lombok.Getter;
import lombok.Setter;
import org.cswteams.ms3.dao.DoctorUffaPrioritySnapshotDAO;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Getter
@Setter
public class DoctorUffaPrioritySnapshot {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;

    /** Doctor which the Uffa priority refers to */
    @OneToOne
    @NotNull
    private Doctor doctor;

    /** Current schedule */
    @OneToOne
    private Schedule schedule;

    private int longShiftPriority = 0;
    private int nightPriority = 0;
    private int generalPriority = 0;

    public DoctorUffaPrioritySnapshot() {}

    public DoctorUffaPrioritySnapshot(Doctor doctor) {this.doctor = doctor;}

}
