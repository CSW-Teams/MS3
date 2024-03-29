package org.cswteams.ms3.dto.shift;

import lombok.Getter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Getter
public class MedicalServiceShiftDTO {

    private Long id ;

    @NotNull
    @NotEmpty
    private final String label ;
    //TODO : Evaluate if this DTO needs to show the Tasks too

    /**
     *
     * @param label The label describing the service
     */
    public MedicalServiceShiftDTO(String label) {
        this.label = label ;
    }

    /**
     *
     * @param id The id of the service, if it exists
     * @param label The label describing the service
     */
    public MedicalServiceShiftDTO(Long id, String label) {
        this.id = id;
        this.label = label;
    }
}
