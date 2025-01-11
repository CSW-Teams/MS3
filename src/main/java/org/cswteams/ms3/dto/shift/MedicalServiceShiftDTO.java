package org.cswteams.ms3.dto.shift;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
public class MedicalServiceShiftDTO {
    @Setter
    private Long id ;

    @NotBlank
    private final String label ;
    //TODO : Evaluate if this DTO needs to show the Tasks too

    /**
     *
     * @param label The label describing the service
     */
    public MedicalServiceShiftDTO(@JsonProperty("label") String label) {
        this.label = label ;
    }

    /**
     *
     * @param id The id of the service, if it exists
     * @param label The label describing the service
     */
    public MedicalServiceShiftDTO(@JsonProperty("id") Long id, @JsonProperty("label") String label) {
        this.id = id;
        this.label = label;
    }

    @Override
    public String toString() {
        return "MedicalServiceShiftDTO{" +
                "id=" + id +
                ", label='" + label +
                "'}";
    }
}
