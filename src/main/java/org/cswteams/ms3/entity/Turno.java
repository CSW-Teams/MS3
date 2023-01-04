package org.cswteams.ms3.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import org.cswteams.ms3.enums.CategoriaUtentiEnum;
import org.cswteams.ms3.enums.TipologiaTurno;

import javax.persistence.*;
import java.time.LocalTime;
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

    /**
     * In quali giorni della settimana questo turno può essere assegnato
     */
    private GiorniDellaSettimanaBitMask giorniDiValidità;

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
        this.giorniDiValidità = (new GiorniDellaSettimanaBitMask()).enableAllDays();

    }

    public Turno(long id,LocalTime oraInizio, LocalTime oraFine, Servizio servizio, TipologiaTurno tipologia, Set<CategoriaUtentiEnum> categorieVietate){
        this(oraInizio, oraFine, servizio, tipologia, categorieVietate);
        this.id = id;
    }

    public Turno(Long id, TipologiaTurno tipologiaTurno, LocalTime oraInizio, LocalTime oraFine,
            GiorniDellaSettimanaBitMask giorniDiValidità, Set<CategoriaUtentiEnum> categorieVietate, Servizio servizio) {
        this(id, oraInizio, oraFine, servizio, tipologiaTurno, categorieVietate);
        this.giorniDiValidità = giorniDiValidità;
    }

    public Turno() {
    }
}
