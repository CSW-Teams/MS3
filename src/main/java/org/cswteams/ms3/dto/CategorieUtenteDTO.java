package org.cswteams.ms3.dto;


import lombok.Data;
import org.cswteams.ms3.enums.CategoriaUtentiEnum;
import java.time.LocalDate;

@Data
public class CategorieUtenteDTO {

    private CategoriaUtentiEnum categoria;

    private LocalDate inizioValidita;

    private LocalDate fineValidita;

    public CategorieUtenteDTO() {
    }

    public CategorieUtenteDTO(CategoriaUtentiEnum categoria, LocalDate inizioValidita, LocalDate fineValidita) {
        this.categoria=categoria;
        this.inizioValidita = inizioValidita;
        this.fineValidita = fineValidita;
    }
}
