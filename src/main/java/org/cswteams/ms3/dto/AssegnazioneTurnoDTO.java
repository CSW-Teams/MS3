package org.cswteams.ms3.dto;

import org.cswteams.ms3.entity.Servizio;
import org.cswteams.ms3.enums.TipologiaTurno;

import java.sql.Timestamp;
import java.util.Set;

public class AssegnazioneTurnoDTO {

    private Long id;
    private Timestamp inizio;
    private Timestamp fine;

    private Set<UtenteDTO> utentiDiGuardia;
    private Set<UtenteDTO> utentiReperibili;

    private ServizioDTO servizio;
    private TipologiaTurno tipologiaTurno;

    public AssegnazioneTurnoDTO(Long id, Timestamp inizio, Timestamp fine, Set<UtenteDTO> utentiDiGuardia, Set<UtenteDTO> utentiReperibili, ServizioDTO servizio, TipologiaTurno turno) {
        this.id = id;
        this.inizio = inizio;
        this.fine = fine;
        this.utentiDiGuardia = utentiDiGuardia;
        this.utentiReperibili = utentiReperibili;
        this.tipologiaTurno = turno;
        this.servizio = servizio;
    }

    public AssegnazioneTurnoDTO(Timestamp inizio, Timestamp fine, Set<UtenteDTO> utentiDiGuardia, Set<UtenteDTO> utentiReperibili, ServizioDTO servizio, TipologiaTurno turno) {
        this.inizio = inizio;
        this.fine = fine;
        this.utentiDiGuardia = utentiDiGuardia;
        this.utentiReperibili = utentiReperibili;
        this.tipologiaTurno = turno;
        this.servizio = servizio;
    }

    public Long getId() {
        return id;
    }

    public Timestamp getInizio() {
        return inizio;
    }

    public Timestamp getFine() {
        return fine;
    }

    public Set<UtenteDTO> getUtentiDiGuardia() {
        return utentiDiGuardia;
    }

    public Set<UtenteDTO> getUtentiReperibili() {
        return utentiReperibili;
    }

    public ServizioDTO getServizio() {
        return servizio;
    }

    public TipologiaTurno getTipologiaTurno() {
        return tipologiaTurno;
    }
}
