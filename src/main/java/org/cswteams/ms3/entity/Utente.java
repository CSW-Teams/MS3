package org.cswteams.ms3.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import javax.persistence.*;
import java.util.List;

@Entity
@Data
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Utente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;
    private String cognome;

    @ManyToOne(cascade = CascadeType.ALL)
    private Ruolo ruolo;

    @ManyToMany(cascade = CascadeType.MERGE)
    @JsonIgnore
    private List<AssegnazioneTurno> turni = new java.util.ArrayList<>();


    protected Utente() {

    }

    public Utente(String nome, String cognome, Ruolo role) {
        this.nome = nome;
        this.cognome = cognome;
        this.ruolo = role;
    }



}
