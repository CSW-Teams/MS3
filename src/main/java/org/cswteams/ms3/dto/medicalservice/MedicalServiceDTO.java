package org.cswteams.ms3.dto.medicalservice;

import lombok.Getter;
import org.cswteams.ms3.entity.Task;

import java.util.ArrayList;
import java.util.List;

@Getter
public class MedicalServiceDTO {

    private String nome;
    private List<Task> mansioni = new ArrayList<>();

    public MedicalServiceDTO(String nome, List<Task> mansioni){
        this.nome = nome;
        this.mansioni = mansioni;
    }

    public MedicalServiceDTO(String nome){
        this.nome = nome;
    }
    public MedicalServiceDTO(){}
}
