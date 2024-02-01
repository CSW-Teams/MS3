package org.cswteams.ms3.entity.constraint;

import org.cswteams.ms3.exception.ViolatedConstraintException;
import javax.persistence.Entity;

/**
 * An additional <i>constraint</i> that can be added whenever it is needed.
 */
@Entity
public class AdditionalConstraint extends Constraint {
    @Override
    public void verifyConstraint(ContextConstraintPriority contesto) throws ViolatedConstraintException {

    }
}
