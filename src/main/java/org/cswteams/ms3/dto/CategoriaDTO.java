package org.cswteams.ms3.dto;

import lombok.Data;

@Data
public class CategoriaDTO {

    private String nome;
    private int tipo;

    public CategoriaDTO() {

    }

    public CategoriaDTO(String nome,int tipo) {
        this.nome=nome;
        this.tipo=tipo;
    }



}
