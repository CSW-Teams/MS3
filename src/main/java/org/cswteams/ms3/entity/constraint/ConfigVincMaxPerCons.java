package org.cswteams.ms3.entity.constraint;

import lombok.Data;
import org.cswteams.ms3.entity.condition.Condition;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

/**
 * This entity models the configuration for the system association
 * between <i>users</i> with a specific <i>Condition</i> and the maximum time of consecutive work for them,
 * e.g. elderly/pregnant <i>users</i> can have a limit to a certain amount of time.
 *
 * @see ConstraintMaxPeriodoConsecutivo for the related constraint
 */
@Entity
@Data
public class ConfigVincMaxPerCons {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "max_periodo_id", nullable = false)
    private Long id;

    @ManyToOne
    @NotNull
    private Condition constrainedCondition;

    /**
     * Maximum consecutive work time for the <code>constrainedCondition</code>, in minutes.
     */
    @Transient
    private int maxConsecutiveMinutes;

    /**
     * Default constructor needed by Lombok
     */
    public ConfigVincMaxPerCons(){

    }

    /**
     * Create a new <i>Condition</i>-max consecutive work time
     *
     * @param constrainedCondition  condition for this constraint
     * @param maxConsecutiveMinutes maximum consecutive work time [minutes]
     */
    public ConfigVincMaxPerCons(Condition constrainedCondition, int maxConsecutiveMinutes) {
        this.constrainedCondition = constrainedCondition;
        this.maxConsecutiveMinutes = maxConsecutiveMinutes;
    }
}
