package org.cswteams.ms3.enums;

/**
 * This enumeration provides all the possible statuses for an assignation of a <i>Doctor</i>
 * to a <i>concrete shift</i>.
 *
 * @see org.cswteams.ms3.entity.ConcreteShift
 */
public enum ConcreteShiftDoctorStatus {
    /**
     * The <i>Doctor</i> is actively on duty.
     */
    ON_DUTY,

    /**
     * The <i>Doctor</i> is available in case of emergency/if needed.
     */
    ON_CALL,

    /**
     * The <i>Doctor</i> was once assigned to this <i>concrete shift</i>, but was later removed.
     */
    REMOVED
}
