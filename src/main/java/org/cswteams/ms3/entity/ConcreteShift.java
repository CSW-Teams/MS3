package org.cswteams.ms3.entity;

import lombok.Getter;
import org.cswteams.ms3.enums.ConcreteShiftDoctorStatus;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

/**
 * This class represents the concrete shift present in a schedule for a certain date.
 * This class should be operated only by the planners.
 * Instantiation of a <i>shift</i>, with some <i>Doctors</i> associated.
 *
 * @see <a href="https://github.com/CSW-Teams/MS3/wiki#assegnazione-turno">Glossary</a>.
 */
@Entity
@Getter
public class ConcreteShift {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "concrete_shift_id", nullable = false)
    private Long id;

    /**
     * Date of the <i>concrete shift</i>.
     * This date is in epoch format to keep track of the timezone
     */
    @NotNull
    private long date;

    /**
     * The <i>shift</i> istantiated by this <i>concrete shift</i>.
     */
    @ManyToOne
    @NotNull
    private Shift shift;

    /**
     * List of all the <i>Doctors</i> involved in this <i>concrete shift</i>.
     */
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @NotNull
    private List<DoctorAssignment> doctorAssignmentList = new ArrayList<>(); //TODO: check that the doctors involved in this list are all different
    // maybe make it a Set<>?


    protected ConcreteShift(Long id) {

        this.id = id;
    }

    /**
     * Create a <i>concrete shift</i> for a specific <i>shift</i> in a specific date.
     *
     * @param date  The date of the concrete shift
     * @param shift The abstract shift from which this shift is created
     */
    public ConcreteShift(Long date, Shift shift) {
        this.date = date;
        this.shift = shift;
        this.doctorAssignmentList = new ArrayList<>();
    }

    /**
     * Create a <i>concrete shift</i> for a specific <i>shift</i> in a specific date,
     * with a specific list of <i>Doctors</i> assigned.
     *
     * @param date                 The date of the concrete shift
     * @param shift                The abstract shift from which this shift is created
     * @param doctorAssignmentList List of <i>Doctors</i> assigned to this <i>concrete shift</i>.
     */
    protected ConcreteShift(Long date, Shift shift, List<DoctorAssignment> doctorAssignmentList) {
        this.date = date;
        this.shift = shift;
        this.doctorAssignmentList = doctorAssignmentList;
    }

    /**
     * Default constructor needed by Lombok
     */
    protected ConcreteShift() {

    }

    @Override
    public ConcreteShift clone() {
        return new ConcreteShift(this.date, this.shift, this.doctorAssignmentList);
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