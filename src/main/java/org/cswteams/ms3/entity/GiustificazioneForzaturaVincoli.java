package org.cswteams.ms3.entity;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import org.cswteams.ms3.entity.vincoli.Vincolo;

import javax.persistence.*;
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

    @OneToMany(cascade = CascadeType.ALL)
    private Set<Liberatoria> liberatorie;

    @OneToOne
    private AssegnazioneTurno assegnazioneViolante;

    @ManyToMany
    private List<Vincolo> vincoliViolati;
    private String motivazione;

    @ManyToOne
    private Utente utenteGiustificatore;

    public GiustificazioneForzaturaVincoli() {

    }


    public GiustificazioneForzaturaVincoli(String message, Utente utenteGiustificante, Set<Liberatoria> liberatorie, List<Vincolo> vincoliViolati, AssegnazioneTurno assegnazioneViolante) {
        this.motivazione=message;
        this.utenteGiustificatore=utenteGiustificante;
        this.liberatorie=liberatorie;
        this.assegnazioneViolante = assegnazioneViolante;
        this.vincoliViolati = vincoliViolati;
    }
}
