package org.cswteams.ms3.entity;


import lombok.Data;
import org.cswteams.ms3.enums.TimeSlot;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Set;

@Data
@Entity
public class GiustificazioneForzaturaVincoli {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

  /*  @OneToMany(cascade = CascadeType.ALL)
    private Set<Waiver> liberatorie;*/

    private TimeSlot turnoViolante;

    private LocalDate data;

    @ManyToMany
    private Set<Doctor> utentiAllocati;

    @ManyToOne
    private MedicalService servizio;

    /*@ManyToMany
    private List<Constraint> vincoliViolati;*/
    private String motivazione;

    @ManyToOne
    private Doctor doctorGiustificatore;

    public GiustificazioneForzaturaVincoli() {

    }


    public GiustificazioneForzaturaVincoli(String message, TimeSlot turnoViolante, MedicalService servizio, LocalDate data, Set<Doctor> utentiAllocati, Doctor doctorGiustificatore) {
        this.turnoViolante = turnoViolante;
        this.data = data;
        this.utentiAllocati = utentiAllocati;
        this.servizio = servizio;
        this.motivazione = message;
        this.doctorGiustificatore = doctorGiustificatore;
    }
}
