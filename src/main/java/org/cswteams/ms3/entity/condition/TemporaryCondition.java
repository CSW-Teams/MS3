package org.cswteams.ms3.entity.condition;

import lombok.Getter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

/**
 * This <i>condition</i> is temporary, e.g. pregnancy and so on.
 */
@Entity
@Table(name = "temporary_condition")
@Getter
public class TemporaryCondition extends Condition {

    /**
     * Start date for the <i>condition</i>, in <i>epoch</i> days.
     */
    @NotNull
    @Column(name = "start_date")
    private long startDate;

    /**
     * End date for the <i>condition</i>, in <i>epoch</i> days.
     */
    @NotNull
    @Column(name = "end_date")
    private long endDate;

    /**
     * Create a temporary <code>condition</code> as descripted by <code>type</code>, .
     *
     * @param type      temporary condition description
     * @param startDate Start date for the <i>condition</i>, in <i>epoch</i> days.
     * @param endDate   End date for the <i>condition</i>, in <i>epoch</i> days.
     */
    public TemporaryCondition(String type, long startDate, long endDate) {
        super(type);
        this.startDate = startDate;
        this.endDate = endDate;
    }

    /**
     * Default constructor needed by Lombok
     */
    protected TemporaryCondition() {

    }
}
