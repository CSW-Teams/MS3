package org.cswteams.ms3.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.List;

@Entity
@Data
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Turno {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToMany
    private List<Utente> utenti;

    private Timestamp inizio;

    private Timestamp fine;


    protected Turno() {

    }

    public Turno(Timestamp inizio, Timestamp fine, List<Utente> utenti) {
        this.inizio = inizio;
        this.fine = fine;
        this.utenti = utenti;
    }

}