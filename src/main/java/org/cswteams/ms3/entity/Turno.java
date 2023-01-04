package org.cswteams.ms3.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import org.cswteams.ms3.enums.CategoriaUtentiEnum;
import org.cswteams.ms3.enums.TipologiaTurno;
import org.cswteams.ms3.exception.TurnoException;

import javax.persistence.*;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;

@Entity
@Data
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Turno {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Long id;

    private TipologiaTurno tipologiaTurno;

    private LocalTime oraInizio;

    private LocalTime oraFine;

    @ManyToOne
    private Servizio servizio;

    @ElementCollection
    @Enumerated(EnumType.STRING)
    private Set<CategoriaUtentiEnum> categorieVietate;

    public Turno(LocalTime oraInizio, LocalTime oraFine, Servizio servizio, TipologiaTurno tipologia, Set<CategoriaUtentiEnum> categorieVietate){
        this.oraInizio = oraInizio;
        this.oraFine = oraFine;
        this.servizio = servizio;
        this.tipologiaTurno = tipologia;
        this.categorieVietate = categorieVietate;

    }

    public Turno(long id,LocalTime oraInizio, LocalTime oraFine, Servizio servizio, TipologiaTurno tipologia, Set<CategoriaUtentiEnum> categorieVietate){
        this(oraInizio, oraFine, servizio, tipologia, categorieVietate);
        this.id = id;

    }

    public Turno() {

    }

    public Long getId() {
        return id;
    }

    public TipologiaTurno getTipologiaTurno() {
        return tipologiaTurno;
    }

    public LocalTime getOraInizio() {
        return oraInizio;
    }

    public LocalTime getOraFine() {
        return oraFine;
    }

    public Servizio getServizio() {
        return servizio;
    }

    public Set<CategoriaUtentiEnum> getCategorieVietate() {
        return categorieVietate;
    }
}
