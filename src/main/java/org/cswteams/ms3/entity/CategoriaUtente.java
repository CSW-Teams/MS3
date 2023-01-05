package org.cswteams.ms3.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import org.cswteams.ms3.enums.CategoriaUtentiEnum;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import java.time.LocalDate;

@Entity
@Table(uniqueConstraints={
    @UniqueConstraint(columnNames={
        "categoria",
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

    private CategoriaUtentiEnum categoria;
   
    private LocalDate inizioValidità;

    private LocalDate fineValidità;

    public CategoriaUtente() {

    }


    public CategoriaUtente(CategoriaUtentiEnum categoria, LocalDate inizioValidità, LocalDate fineValidità) {
        this.categoria = categoria;
        this.inizioValidità = inizioValidità;
        this.fineValidità = fineValidità;
    }

}
