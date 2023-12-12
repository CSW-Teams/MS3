package org.cswteams.ms3.entity;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import org.cswteams.ms3.entity.doctor.Doctor;
import org.cswteams.ms3.enums.TipologiaTurno;

import javax.persistence.*;
import java.time.LocalDate;
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
    private Set<Doctor> utentiAllocati;

    @ManyToOne
    private Servizio servizio;

    /*@ManyToMany
    private List<Vincolo> vincoliViolati;*/
    private String motivazione;

    @ManyToOne
    private Doctor doctorGiustificatore;

    public GiustificazioneForzaturaVincoli() {

    }


    public GiustificazioneForzaturaVincoli(String message, TipologiaTurno turnoViolante, Servizio servizio, LocalDate data, Set<Doctor> utentiAllocati, Doctor doctorGiustificatore) {
        this.turnoViolante = turnoViolante;
        this.data = data;
        this.utentiAllocati = utentiAllocati;
        this.servizio = servizio;
        this.motivazione = message;
        this.doctorGiustificatore = doctorGiustificatore;
    }
}
