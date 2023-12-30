package org.cswteams.ms3.dto.shift;

import lombok.Getter;

@Getter
public class AdditionalConstraintShiftDTO {

    private Long id ;
    private final boolean violable ;
    private final String description ;

    /**
     *
     * @param violable If the constraint will be violable
     * @param description The constraint's description
     */
    public AdditionalConstraintShiftDTO(boolean violable, String description) {
        this.violable = violable;
        this.description = description;
    }

    /**
     *
     * @param id The id of the constraint, if necessary
     * @param violable If the constraint will be violable
     * @param description The constraint's description
     */
    public AdditionalConstraintShiftDTO(Long id, boolean violable, String description) {
        this.id = id;
        this.violable = violable;
        this.description = description;
    }
}
