package org.cswteams.ms3.entity.constraint;

import lombok.Data;
import org.cswteams.ms3.exception.ViolatedConstraintException;
import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Data
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@Table(name = "ms3_constraint")
public abstract class MS3Constraint {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "constraint_id_seq")
    @SequenceGenerator(name = "constraint_id_seq", sequenceName = "constraint_id_seq")
    @NotNull
    @Column(name = "constraint_id", nullable = false)
    private Long id;

    /** True se il vincolo è non-stringente */
    @NotNull
    private boolean violabile = false;

    /** TODO: descrizione di che cosa in particolare del vincolo? */
    @NotNull
    private String descrizione;

    /**
     * @throws ViolatedConstraintException : se il vincolo è violato
     */
    public abstract void verificaVincolo(ContestoVincolo contesto) throws ViolatedConstraintException;
}
