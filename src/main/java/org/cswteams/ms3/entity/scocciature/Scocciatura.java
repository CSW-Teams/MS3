package org.cswteams.ms3.entity.scocciature;

import lombok.Data;
import org.cswteams.ms3.exception.ViolatedConstraintException;

import javax.persistence.*;

@Entity
@Data
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public abstract class  Scocciatura {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Long id;

    /**
     * Calcola quanto peso ad un utente essere associato a un assegnazione turno
     */
    public abstract int calcolaUffa(ContestoScocciatura contesto) ;
}
