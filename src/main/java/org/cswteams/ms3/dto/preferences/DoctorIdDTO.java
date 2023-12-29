package org.cswteams.ms3.dto.preferences;

import lombok.Getter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;

@Getter
public class DoctorIdDTO {

    @NotNull
    @PositiveOrZero
    private final Long doctorId ;

    /**
     * @param doctorId The id of the doctor to pass
     */
    public DoctorIdDTO(Long doctorId) {
        this.doctorId = doctorId;
    }
}
