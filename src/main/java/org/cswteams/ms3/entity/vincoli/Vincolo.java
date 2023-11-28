package org.cswteams.ms3.entity.vincoli;

import lombok.Data;
import org.cswteams.ms3.exception.ViolatedConstraintException;
import javax.persistence.*;

@Entity
@Data
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public abstract class  Vincolo {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Long id;

    /** True se il vincolo è non-stringente */
    private boolean violabile = false;

    /** TODO: descrizione di che cosa in particolare del vincolo? */
    private String descrizione;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    /**
     * @throws ViolatedConstraintException : se il vincolo è violato
     */
    public abstract void verificaVincolo(ContestoVincolo contesto) throws ViolatedConstraintException;
}
