package org.cswteams.ms3.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import org.cswteams.ms3.enums.CategoriaUtentiEnum;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import java.time.LocalDate;

@Entity
@IdClass(CategoriaUtenteId.class)
@Data
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class CategoriaUtente {

    @Id
    private CategoriaUtentiEnum categoria;

    @Id
    private Long utenteId;

    private LocalDate inizioValidità;

    private LocalDate fineValidità;

    public CategoriaUtente() {
    }

    public CategoriaUtente(CategoriaUtentiEnum categoria, Long utenteId, LocalDate inizioValidità, LocalDate fineValidità) {
        this.categoria = categoria;
        this.utenteId = utenteId;
        this.inizioValidità = inizioValidità;
        this.fineValidità = fineValidità;
    }

    public CategoriaUtentiEnum getCategoria() {
        return categoria;
    }

    public Long getUtenteId() {
        return utenteId;
    }

    public LocalDate getInizioValidità() {
        return inizioValidità;
    }

    public LocalDate getFineValidità() {
        return fineValidità;
    }
}
