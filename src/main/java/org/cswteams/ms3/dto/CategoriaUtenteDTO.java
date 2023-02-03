package org.cswteams.ms3.dto;


import lombok.Data;
import org.cswteams.ms3.entity.Categoria;

import java.time.LocalDate;

@Data
public class CategoriaUtenteDTO {

    private Long id;

    private Categoria categoria;

    private String inizioValidita;

    private String fineValidita;



    public CategoriaUtenteDTO() {
    }

    public CategoriaUtenteDTO(Categoria  categoria, String inizioValidita, String fineValidita) {
        this.categoria=categoria;
        this.inizioValidita = inizioValidita;
        this.fineValidita = fineValidita;
    }

    public CategoriaUtenteDTO(Long id, Categoria categoria, String inizioValidita, String fineValidita) {
        this(categoria,inizioValidita,fineValidita);
        this.id = id;

    }
}
