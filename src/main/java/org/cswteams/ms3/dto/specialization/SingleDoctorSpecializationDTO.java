package org.cswteams.ms3.dto.specialization;

import lombok.Getter;

import java.util.Set;

// DTO sent in POST request from client to server to delete a specialization of a doctor
@Getter
public class SingleDoctorSpecializationDTO {
    private Long doctorID;
    private String specialization;

    /**
     * This DTO brings the information to the backend needed to delete the specialization of a doctor
     * @param doctorID The ID with which the doctor is identified in the DB
     * @param specialization The string representing the specialization
     */
    public SingleDoctorSpecializationDTO(Long doctorID, String specialization) {
        this.doctorID = doctorID;
        this.specialization = specialization;
    }

}
