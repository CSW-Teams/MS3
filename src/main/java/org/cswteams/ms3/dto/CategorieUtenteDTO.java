package org.cswteams.ms3.dto;


import lombok.Data;
import org.cswteams.ms3.entity.Categoria;

import java.time.LocalDate;
import java.util.HashSet;

@Data
public class CategorieUtenteDTO {

    private Categoria categoria;

    private LocalDate inizioValidita;

    private LocalDate fineValidita;



    public CategorieUtenteDTO() {
    }

    public CategorieUtenteDTO(Categoria  categoria, LocalDate inizioValidita, LocalDate fineValidita) {
        this.categoria=categoria;
        this.inizioValidita = inizioValidita;
        this.fineValidita = fineValidita;
    }

}
