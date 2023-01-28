package org.cswteams.ms3.entity;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

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
    private Set<Files> delibere;

    private String message;

    @OneToOne
    private Utente utenteGiustificatore;

    public GiustificazioneForzaturaVincoli() {

    }


    public GiustificazioneForzaturaVincoli(String message, Utente utenteGiustificante, Set<Files> delibere) {
        this.message=message;
        this.utenteGiustificatore=utenteGiustificante;
        this.delibere=delibere;
    }
}
