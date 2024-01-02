package org.cswteams.ms3.dto;

import lombok.Data;
import org.cswteams.ms3.enums.MansioneEnum;

import java.util.ArrayList;
import java.util.List;

@Data
public class ServizioDTO {

    private String nome;
    private List<MansioneEnum> mansioni = new ArrayList<>();

    public ServizioDTO(String nome, List<MansioneEnum> mansioni){
        this.nome = nome;
        this.mansioni = mansioni;
    }

    public ServizioDTO(String nome){
        this.nome = nome;
    }
    public ServizioDTO(){}
}
