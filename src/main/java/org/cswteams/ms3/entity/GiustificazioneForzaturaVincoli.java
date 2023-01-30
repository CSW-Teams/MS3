package org.cswteams.ms3.entity;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import org.cswteams.ms3.entity.vincoli.Vincolo;
import org.cswteams.ms3.enums.TipologiaTurno;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Data
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Entity
public class GiustificazioneForzaturaVincoli {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

  /*  @OneToMany(cascade = CascadeType.ALL)
    private Set<Liberatoria> liberatorie;*/

    private TipologiaTurno turnoViolante;

    private LocalDate data;

    @ManyToMany
    private Set<Utente> utentiAllocati;

    @ManyToOne
    private Servizio servizio;

    /*@ManyToMany
    private List<Vincolo> vincoliViolati;*/
    private String motivazione;

    @ManyToOne
    private Utente utenteGiustificatore;

    public GiustificazioneForzaturaVincoli() {

    }


    public GiustificazioneForzaturaVincoli(String message, TipologiaTurno turnoViolante, Servizio servizio, LocalDate data, Set<Utente> utentiAllocati, Utente utenteGiustificatore) {
        this.turnoViolante = turnoViolante;
        this.data = data;
        this.utentiAllocati = utentiAllocati;
        this.servizio = servizio;
        this.motivazione = message;
        this.utenteGiustificatore = utenteGiustificatore;
    }
}
