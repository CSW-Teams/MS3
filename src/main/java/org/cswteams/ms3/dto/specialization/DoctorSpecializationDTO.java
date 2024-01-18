package org.cswteams.ms3.dto.specialization;

import lombok.Getter;

import java.util.Set;

// DTO sent in DELETE request from client to server to delete a specialization of a doctor
@Getter
public class DoctorSpecializationDTO {
    private Long doctorID;
    private Set<String> specializations;

    /**
     * This DTO brings the information to the backend needed to delete the specialization of a doctor
     * @param doctorID The ID with which the doctor is identified in the DB
     * @param specialization The set of strings representing the specialization
     */
    public DoctorSpecializationDTO(Long doctorID, Set<String> specialization) {
        this.doctorID = doctorID;
        this.specializations = specialization;
    }

}
