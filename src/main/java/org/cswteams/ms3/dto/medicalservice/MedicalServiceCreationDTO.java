package org.cswteams.ms3.dto.medicalservice;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class MedicalServiceCreationDTO {

    private String name;

    private List<String> taskTypes = new ArrayList<>();

    public MedicalServiceCreationDTO(String name, List<String> taskTypes) {
        this.name = name;
        this.taskTypes = taskTypes;
    }

    public MedicalServiceCreationDTO(){}
}
