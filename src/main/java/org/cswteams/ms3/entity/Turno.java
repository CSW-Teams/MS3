package org.cswteams.ms3.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import org.cswteams.ms3.enums.TipologiaTurno;

import javax.persistence.*;
import java.time.LocalTime;

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

    public Turno(LocalTime oraInizio, LocalTime oraFine, Servizio servizio, TipologiaTurno tipologia){
        this.oraInizio = oraInizio;
        this.oraFine = oraFine;
        this.servizio = servizio;
        this.tipologiaTurno = tipologia;
        this.giorniDiValidità = (new GiorniDellaSettimanaBitMask()).enableAllDays();

    }

    public Turno(long id,LocalTime oraInizio, LocalTime oraFine, Servizio servizio, TipologiaTurno tipologia){
        this(oraInizio, oraFine, servizio, tipologia);
        this.id = id;
    }

    public Turno(Long id, TipologiaTurno tipologiaTurno, LocalTime oraInizio, LocalTime oraFine,
            GiorniDellaSettimanaBitMask giorniDiValidità, Servizio servizio) {
        this(id, oraInizio, oraFine, servizio, tipologiaTurno);
        this.giorniDiValidità = giorniDiValidità;
    }

    public Turno() {
    }
}
