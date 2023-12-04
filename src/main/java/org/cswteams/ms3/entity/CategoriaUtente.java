package org.cswteams.ms3.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import org.cswteams.ms3.jpa_constraints.temporal_consistency.BeforeInTime;
import org.cswteams.ms3.jpa_constraints.temporal_consistency.DateTimeComparator;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

import java.time.LocalDate;

@Entity
@Table(uniqueConstraints={
    @UniqueConstraint(columnNames={
            "categoria_id",
        "inizioValidità",
        "fineValidità"
    })
})

@Data
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@BeforeInTime(firstParam = "inizioValidità", secondParam = "fineValidità", comparator = DateTimeComparator.class)
public class CategoriaUtente {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    @NotNull
    private Categoria categoria;

    @NotNull
    private LocalDate inizioValidità;

    @NotNull
    private LocalDate fineValidità;

    public CategoriaUtente() {

    }


    public CategoriaUtente(Categoria categoria, LocalDate inizioValidità, LocalDate fineValidità) {
        this.categoria = categoria;
        this.inizioValidità = inizioValidità;
        this.fineValidità = fineValidità;
    }

    /**
     * Verifica se la categoria è valida in una dato giorno.
     * @param when Giorno per cui si vuole verificare la validità della categoria
     * @return {@code true} se la categoria è valida in quel giorno, {@code false} altrimenti
     */
    public boolean isValid(LocalDate when){

            return when.compareTo(inizioValidità) >= 0 && when.compareTo(fineValidità) <= 0;
    }
}
