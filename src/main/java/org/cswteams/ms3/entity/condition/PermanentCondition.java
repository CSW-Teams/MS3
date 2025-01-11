package org.cswteams.ms3.entity.condition;

import lombok.Getter;

import javax.persistence.*;

/**
 * This <i>condition</i> is permament, e.g. being senior ("over 62") and so on.
 */
@Entity
@Table(name = "permanent_condition")
@Getter
public class PermanentCondition extends Condition {


    /**
     * Create a permament <code>condition</code> as descripted by <code>type</code>.
     * @param type permanent condition description
     */
    public PermanentCondition(String type) {
        super(type);
    }

    /**
     * Default constructor needed by Lombok
     */
    protected PermanentCondition() {
    }
}
