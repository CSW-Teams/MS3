package org.cswteams.ms3.entity;

import javax.persistence.*;

import org.cswteams.ms3.exception.ViolatedConstraintException;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

/**
 * A Log entry for constraint violations.
 */
@Data
@Entity
@NoArgsConstructor(force = true)
@RequiredArgsConstructor
@Table(name = "violated_constraint_log_entry")
public class ViolatedConstraintLogEntry {

    @Id
    @GeneratedValue
    private Long id;
    
    /**
     * Each constraint violation is modeled by the corresponding Exception
     */
    @Column(name = "violated_constraint_exception")
    private final ViolatedConstraintException violation;

    /** Is the violation corresponding to a strict constraint? */
    // TODO: non so se serve, intanto sta qua
    //private boolean isStrict;

}
