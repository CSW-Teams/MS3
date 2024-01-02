package org.cswteams.ms3.dto;

import lombok.Data;
import org.cswteams.ms3.enums.TipoCategoriaEnum;

@Data
public class CategoriaDTO {

    private String nome;
    private TipoCategoriaEnum tipo;

    public CategoriaDTO() {

    }

    public CategoriaDTO(String nome,TipoCategoriaEnum tipo) {
        this.nome=nome;
        this.tipo=tipo;
    }



}
