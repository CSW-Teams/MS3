package org.cswteams.ms3.control.scheduler;

import org.cswteams.ms3.entity.constraint.Constraint;

/**
 * Defines which constraint categories are blocking during schedule validation.
 */
public enum ConstraintEnforcementMode {

    /**
     * Hard constraints are blocking, soft constraints are non-blocking.
     */
    HARD_ONLY,

    /**
     * Hard and soft constraints are both blocking.
     */
    HARD_AND_SOFT;

    public boolean isBlocking(Constraint constraint) {
        boolean hardConstraint = !constraint.isViolable();
        boolean softConstraint = constraint.isViolable();

        return hardConstraint || (this == HARD_AND_SOFT && softConstraint);
    }
}
