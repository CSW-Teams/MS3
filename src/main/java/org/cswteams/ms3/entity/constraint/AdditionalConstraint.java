package org.cswteams.ms3.entity.constraint;

import org.cswteams.ms3.exception.ViolatedConstraintException;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Rappresenta un vincolo aggiuntivo ({@code AdditionalConstraint}) che può essere esteso
 * in futuro. Attualmente, è privo di logica di verifica e funge da placeholder.
 *
 * Fa parte del "Catalogo vincoli attivi" (Microtask 1.2).
 *
 * @see docs/AI_powered_rescheduling/sprint_4/story_1.md#microtask-12--vincoli-e-pipeline-priorità-baseline
 */
@Entity
public class AdditionalConstraint extends Constraint {    /**
     * Attualmente, questo metodo non contiene logica di verifica e funge da placeholder.
     * In futuro, potrà essere esteso per implementare logiche di vincolo aggiuntive.
     *
     * @param contesto Il contesto su cui applicare la verifica.
     * @throws ViolatedConstraintException Eccezione non lanciata in questa implementazione placeholder.
     */
    @Override
    public void verifyConstraint(ContextConstraint contesto) throws ViolatedConstraintException {

    }
}
