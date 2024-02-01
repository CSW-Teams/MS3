package org.cswteams.ms3.entity.constraint;

import lombok.Getter;
import lombok.Setter;
import org.cswteams.ms3.exception.ViolatedConstraintException;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

/**
 * A condition, imposed on some pianification parameter.
 *
 * @see <a href="https://github.com/CSW-Teams/MS3/wiki#vincoli">Glossary</a>.
 */
@Entity
@Getter
@Setter
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@Table(name = "ms3_constraint")
public abstract class Constraint {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "constraint_id_seq")
    @SequenceGenerator(name = "constraint_id_seq", sequenceName = "constraint_id_seq")
    @NotNull
    @Column(name = "constraint_id", nullable = false)
    private Long id;

    /**
     * Violability of the <i>constraint</i>.
     * <code>true</code>if the <i>constraint</i> is not-stringent (i.e. can be violated).
     */
    @NotNull
    private boolean violable = false;

    /**
     * TODO: descrizione di che cosa in particolare del vincolo?
     */
    @NotNull
    private String description;

    /**
     * Check if the <i>constraint</i> is violated.
     *
     * @throws ViolatedConstraintException if the <i>constraint</i> is violated.
     */
    public abstract void verifyConstraint(ContextConstraintPriority contesto) throws ViolatedConstraintException;
}
