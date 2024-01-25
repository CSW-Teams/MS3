package org.cswteams.ms3.dto.medicalDoctor;

import lombok.Getter;
import org.cswteams.ms3.enums.Seniority;

@Getter
public class MedicalDoctorInfoDTO {

    private Long id;
    private String name;
    private String lastname;
    private Seniority seniority;
    private String task;

    public MedicalDoctorInfoDTO(Long id, String name, String lastname, Seniority seniority, String task) {
        this.id = id;
        this.name = name;
        this.lastname = lastname;
        this.seniority = seniority;
        this.task = task;
    }
}
