package org.cswteams.ms3.entity.vincoli;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.cswteams.ms3.exception.ViolatedConstraintException;
import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Data
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public abstract class  Vincolo {
    @Setter
    @Getter
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "constraint_id_seq")
    @SequenceGenerator(name = "constraint_id_seq", sequenceName = "constraint_id_seq")
    @NotNull
    @Column(name = "id", nullable = false)
    private Long id;

    /** True se il vincolo è non-stringente */
    private boolean violabile = false;

    /** TODO: descrizione di che cosa in particolare del vincolo? */
    private String descrizione;

    /**
     * @throws ViolatedConstraintException : se il vincolo è violato
     */
    public abstract void verificaVincolo(ContestoVincolo contesto) throws ViolatedConstraintException;
}
