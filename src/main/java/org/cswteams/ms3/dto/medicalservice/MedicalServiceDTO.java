package org.cswteams.ms3.dto.medicalservice;

import lombok.Getter;
import org.cswteams.ms3.entity.Task;

import java.util.ArrayList;
import java.util.List;

/**
 * See <code>MedicalServiceCreationDTO</code> if you want to manage only name and task types.
 */
@Getter
public class MedicalServiceDTO {

    private Long id;
    private String name;
    private List<Task> tasks = new ArrayList<>();

    public MedicalServiceDTO(Long id, String name, List<Task> tasks) {
        this.id = id;
        this.name = name;
        this.tasks = tasks;
    }

    public MedicalServiceDTO(String name, List<Task> tasks){
        this.id = null;
        this.name = name;
        this.tasks = tasks;
    }

    public MedicalServiceDTO(String name){
        this.name = name;
    }
    public MedicalServiceDTO(){}
}
