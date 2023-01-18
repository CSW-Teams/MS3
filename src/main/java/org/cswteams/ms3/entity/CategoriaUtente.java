package org.cswteams.ms3.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import javax.persistence.*;

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

public class CategoriaUtente {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    private Categoria categoria;
   
    private LocalDate inizioValidità;

    private LocalDate fineValidità;

    public CategoriaUtente() {

    }


    public CategoriaUtente(Categoria categoria, LocalDate inizioValidità, LocalDate fineValidità) {
        this.categoria = categoria;
        this.inizioValidità = inizioValidità;
        this.fineValidità = fineValidità;
    }

}
