package org.cswteams.ms3.dto;

import lombok.Data;

@Data
public class ServizioDTO {

    private String nome;

    public ServizioDTO(String nome){
        this.nome = nome;
    }
    public ServizioDTO(){}
    public String getNome(){
        return nome;
    }
}
