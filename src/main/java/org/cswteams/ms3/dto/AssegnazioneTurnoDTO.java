package org.cswteams.ms3.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import lombok.Getter;
import org.cswteams.ms3.enums.MansioneEnum;
import org.cswteams.ms3.enums.TipologiaTurno;

import java.sql.Timestamp;
import java.util.Set;

@Data
@AllArgsConstructor
public class AssegnazioneTurnoDTO {

    @Getter
    private Long id;
    @Getter
    private Long idTurno;
    @Getter
    private Timestamp inizio;
    @Getter
    private Timestamp fine;

    private Set<UtenteDTO> utentiDiGuardia;
    private Set<UtenteDTO> utentiReperibili;

    @Getter
    private ServizioDTO servizio;
    @Getter
    private TipologiaTurno tipologiaTurno;

    private MansioneEnum mansione;

    private boolean reperibilitaAttiva;

    private Set<UtenteDTO> retiredUsers;

    public AssegnazioneTurnoDTO(Long id, Timestamp inizio, Timestamp fine, Set<UtenteDTO> utentiDiGuardia, Set<UtenteDTO> utentiReperibili, ServizioDTO servizio, TipologiaTurno turno) {
        this.id = id;
        this.inizio = inizio;
        this.fine = fine;
        this.utentiDiGuardia = utentiDiGuardia;
        this.utentiReperibili = utentiReperibili;
        this.tipologiaTurno = turno;
        this.servizio = servizio;
    }

    public AssegnazioneTurnoDTO(Long id,Long idTurno, Timestamp inizio, Timestamp fine, Set<UtenteDTO> utentiDiGuardia, Set<UtenteDTO> utentiReperibili, ServizioDTO servizio, TipologiaTurno turno, boolean reperibilitaAttiva) {
        this.id = id;
        this.inizio = inizio;
        this.fine = fine;
        this.utentiDiGuardia = utentiDiGuardia;
        this.utentiReperibili = utentiReperibili;
        this.tipologiaTurno = turno;
        this.servizio = servizio;
        this.idTurno=idTurno;
        this.reperibilitaAttiva = reperibilitaAttiva;
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

}
