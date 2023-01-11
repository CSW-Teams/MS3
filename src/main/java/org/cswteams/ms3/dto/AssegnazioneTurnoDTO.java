package org.cswteams.ms3.dto;

import lombok.Data;
import org.cswteams.ms3.enums.TipologiaTurno;

import java.sql.Timestamp;
import java.util.Set;

@Data
public class AssegnazioneTurnoDTO {

    private Long id;
    private Long idTurno;
    private Timestamp inizio;
    private Timestamp fine;

    private Set<UtenteDTO> utentiDiGuardia;
    private Set<UtenteDTO> utentiReperibili;

    private ServizioDTO servizio;
    private TipologiaTurno tipologiaTurno;
    private boolean giornoSuccessivoTurno;

    public AssegnazioneTurnoDTO(Long id, Timestamp inizio, Timestamp fine, Set<UtenteDTO> utentiDiGuardia, Set<UtenteDTO> utentiReperibili, ServizioDTO servizio, TipologiaTurno turno) {
        this.id = id;
        this.inizio = inizio;
        this.fine = fine;
        this.utentiDiGuardia = utentiDiGuardia;
        this.utentiReperibili = utentiReperibili;
        this.tipologiaTurno = turno;
        this.servizio = servizio;
    }

    public AssegnazioneTurnoDTO(Long id,Long idTurno, Timestamp inizio, Timestamp fine, Set<UtenteDTO> utentiDiGuardia, Set<UtenteDTO> utentiReperibili, ServizioDTO servizio, TipologiaTurno turno, boolean giornoSuccessivoTurno) {
        this.id = id;
        this.inizio = inizio;
        this.fine = fine;
        this.utentiDiGuardia = utentiDiGuardia;
        this.utentiReperibili = utentiReperibili;
        this.tipologiaTurno = turno;
        this.servizio = servizio;
        this.idTurno=idTurno;
        this.giornoSuccessivoTurno = giornoSuccessivoTurno;
    }

    public AssegnazioneTurnoDTO(){}

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
    public Long getIdTurno() {
        return idTurno;
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
