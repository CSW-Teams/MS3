package org.cswteams.ms3.dto;

import org.cswteams.ms3.enums.TipologiaTurno;

import java.sql.Timestamp;
import java.time.LocalTime;

public class TurnoDTO {

    private TipologiaTurno tipologiaTurno;

    private LocalTime oraInizio;

    private LocalTime oraFine;

    private ServizioDTO servizio;

    public TurnoDTO(TipologiaTurno tipologiaTurno, LocalTime inizio, LocalTime fine, ServizioDTO servizio){
        this.oraFine = fine;
        this.oraInizio = inizio;
        this.servizio = servizio;
        this.tipologiaTurno = tipologiaTurno;
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

    public ServizioDTO getServizio() {
        return servizio;
    }
}
