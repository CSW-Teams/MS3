package org.cswteams.ms3.dto.medicalservice;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
/**
 * See <code>MedicalServiceCreationDTO</code> if you want to manage only name and task types.
 */
@Getter
public class MedicalServiceWithTaskAssignmentsDTO {

    private Long id;
    private String nome;
    private List<TaskWithAssignmentDTO> mansioni = new ArrayList<>();

    public MedicalServiceWithTaskAssignmentsDTO(Long id, String nome, List<TaskWithAssignmentDTO> mansioni) {
        this.id = id;
        this.nome = nome;
        this.mansioni = mansioni;
    }

    public MedicalServiceWithTaskAssignmentsDTO(String nome, List<TaskWithAssignmentDTO> mansioni){
        this.id = null;
        this.nome = nome;
        this.mansioni = mansioni;
    }

    public MedicalServiceWithTaskAssignmentsDTO(String nome){
        this.nome = nome;
    }
    public MedicalServiceWithTaskAssignmentsDTO(){}
}
