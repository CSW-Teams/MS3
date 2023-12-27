package org.cswteams.ms3.entity;


import lombok.Getter;
import org.cswteams.ms3.enums.ConcreteShiftDoctorStatus;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

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

    @OneToMany
    @NotNull
    private final List<DoctorAssignment> doctorAssignmentList = new ArrayList<>();

    
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

    /**
     * Given a <code>Doctor</code>, return the <code>ConcreteShiftDoctorStatus</code> for which he/she
     * is assigned for this <code>ConcreteShift</code>.
     * If the <code>Doctor</code> provided is not assigned to this <code>ConcreteShift</code>, <code>null</code> is returned.
     *
     * @param doctor doctor for which the lookup is to be done
     * @return the <code>ConcreteShiftDoctorStatus</code> for the doctor, or <code>null</code> if not found for this <code>ConcreteShift</code>.
     */
    public ConcreteShiftDoctorStatus getDoctorAssignmentStatus(Doctor doctor) {
        for (DoctorAssignment doctorAssignment : this.doctorAssignmentList) {
            if (doctorAssignment.getDoctor() == doctor) {
                return doctorAssignment.getConcreteShiftDoctorStatus();
            }
        }
        return null;
    }

    /**
     * Given a <code>Doctor</code>, check if he/she is actively assigned to this <code>ConcreteShift</code>,
     * i.e. is either <i>on duty</i> or <i>on call</i> for it.
     * <p>
     * If the <code>Doctor</code> is removed, it is not actively assigned.
     *
     * @param doctor doctor for which the lookup is to be done
     * @return <code>true</code> if <code>doctor</code> is <i>on duty</i> or <i>on call</i> for this <code>ConcreteShift</code>,
     * <code>false</code> elsewhere.
     */
    public boolean isDoctorAssigned(Doctor doctor) {
        return (getDoctorAssignmentStatus(doctor) == ConcreteShiftDoctorStatus.ON_DUTY
                || getDoctorAssignmentStatus(doctor) == ConcreteShiftDoctorStatus.ON_CALL);
    }
}