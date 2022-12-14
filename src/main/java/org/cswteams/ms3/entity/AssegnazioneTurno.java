package org.cswteams.ms3.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import javax.persistence.*;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Entity
@Data
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class AssegnazioneTurno {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToMany
    private Set<Utente> utentiDiGuardia;

    @ManyToMany
    private Set<Utente> utentiReperibili;

    private LocalDate data;

    protected AssegnazioneTurno() {

    }

    public AssegnazioneTurno(LocalDate data, Set<Utente> utentiReperibili, Set<Utente> utentiDiGuardia) {
        this.data = data;
        this.utentiDiGuardia = utentiDiGuardia;
        this.utentiReperibili = utentiReperibili;
    }

}