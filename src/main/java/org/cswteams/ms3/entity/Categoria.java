package org.cswteams.ms3.entity;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import org.cswteams.ms3.enums.TipoCategoriaEnum;

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

    private TipoCategoriaEnum tipo;

    public Categoria() {
    }

    public Categoria(String nome, TipoCategoriaEnum tipo) {
        this.nome=nome;
        this.tipo=tipo;
    }

}
