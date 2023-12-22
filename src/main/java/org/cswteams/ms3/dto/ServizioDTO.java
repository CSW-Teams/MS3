package org.cswteams.ms3.dto;

import lombok.Data;
import org.cswteams.ms3.entity.Task;

import java.util.ArrayList;
import java.util.List;

@Data
public class ServizioDTO {

    private String nome;
    private List<Task> tasks = new ArrayList<>();

    public ServizioDTO(String nome, List<Task> tasks){
        this.nome = nome;
        this.tasks = tasks;
    }

    public ServizioDTO(String nome){
        this.nome = nome;
    }
    public ServizioDTO(){}
}
