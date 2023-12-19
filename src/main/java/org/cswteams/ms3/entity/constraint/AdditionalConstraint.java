package org.cswteams.ms3.entity.constraint;


import org.cswteams.ms3.exception.ViolatedConstraintException;

import javax.persistence.Entity;

@Entity
public class AdditionalConstraint extends Constraint{
    @Override
    public void verificaVincolo(ContestoVincolo contesto) throws ViolatedConstraintException {

    }
}
