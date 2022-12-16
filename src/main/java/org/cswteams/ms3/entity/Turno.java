package org.cswteams.ms3.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import org.cswteams.ms3.enums.Servizio;
import org.cswteams.ms3.enums.TipologiaTurno;
import org.cswteams.ms3.exception.TurnoException;

import javax.persistence.*;
import java.lang.reflect.UndeclaredThrowableException;
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

    private Servizio servizio;

    public Turno(LocalTime oraInizio, LocalTime oraFine, Servizio servizio, TipologiaTurno tipologia) throws TurnoException {
        if(oraInizio.isAfter(oraFine)){
            throw new TurnoException("Ora inizio dopo ora fine");
        }
        else {
            this.oraInizio = oraInizio;
            this.oraFine = oraFine;
            this.servizio = servizio;
            this.tipologiaTurno = tipologia;
        }
    }

    public Turno() {

    }

    public LocalTime getOraInizio(){
        return this.oraInizio;
    }

    public LocalTime getOraFine(){
        return this.oraFine;
    }

    public Long getId() {
        return id;
    }

    public TipologiaTurno getTipologiaTurno() {
        return tipologiaTurno;
    }

    public Servizio getServizio() {
        return servizio;
    }
}
