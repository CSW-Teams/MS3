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
    private String nome;
    private List<Task> mansioni = new ArrayList<>();

    public MedicalServiceDTO(Long id, String nome, List<Task> mansioni) {
        this.id = id;
        this.nome = nome;
        this.mansioni = mansioni;
    }

    public MedicalServiceDTO(String nome, List<Task> mansioni){
        this.id = null;
        this.nome = nome;
        this.mansioni = mansioni;
    }

    public MedicalServiceDTO(String nome){
        this.nome = nome;
    }
    public MedicalServiceDTO(){}
}
