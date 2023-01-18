package org.cswteams.ms3.entity;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import javax.persistence.*;

@Entity
@Data
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Categoria {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    private String nome;

    private int tipo; //0 : STATO - 1: SPECILIZZAZIONE - 2 TURNAZIONE

    public Categoria() {
    }

    public Categoria(String nome, int tipo) {
        this.nome=nome;
        this.tipo=tipo;
    }

}
