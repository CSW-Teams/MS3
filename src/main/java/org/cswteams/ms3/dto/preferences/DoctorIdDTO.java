package org.cswteams.ms3.dto.preferences;

import lombok.Getter;

@Getter
public class DoctorIdDTO {

    private final Long doctorId ;

    /**
     * @param doctorId The id of the doctor to pass
     */
    public DoctorIdDTO(Long doctorId) {
        this.doctorId = doctorId;
    }
}
