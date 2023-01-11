package org.cswteams.ms3.dto;


import lombok.Data;
import org.cswteams.ms3.enums.CategoriaUtentiEnum;
import java.time.LocalDate;

@Data
public class CategorieUtenteDTO {

    private CategoriaUtentiEnum categoria;

    private LocalDate inizioValidità;

    private LocalDate fineValidità;

    public CategorieUtenteDTO() {
    }

    public CategorieUtenteDTO(CategoriaUtentiEnum categoria, LocalDate inizioValidità, LocalDate fineValidità) {
        this.categoria=categoria;
        this.inizioValidità=inizioValidità;
        this.fineValidità=fineValidità;
    }
}
