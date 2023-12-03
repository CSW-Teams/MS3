package org.cswteams.ms3.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import lombok.Getter;
import org.cswteams.ms3.enums.MansioneEnum;
import org.cswteams.ms3.enums.TipologiaTurno;

import java.util.Set;

@Data
@AllArgsConstructor
public class AssegnazioneTurnoDTO {

    @Getter
    private Long id;
    @Getter
    private Long idTurno;
    @Getter
    private long inizioEpoch;
    @Getter
    private long fineEpoch;

    private Set<UtenteDTO> utentiDiGuardia;
    private Set<UtenteDTO> utentiReperibili;

    @Getter
    private ServizioDTO servizio;
    @Getter
    private TipologiaTurno tipologiaTurno;

    private MansioneEnum mansione;

    private boolean reperibilitaAttiva;

    private Set<UtenteDTO> retiredUsers;

    public AssegnazioneTurnoDTO(Long id, long inizioEpoch, long fineEpoch, Set<UtenteDTO> utentiDiGuardia, Set<UtenteDTO> utentiReperibili, ServizioDTO servizio, TipologiaTurno turno) {
        this.id = id;
        this.inizioEpoch = inizioEpoch;
        this.fineEpoch = fineEpoch;
        this.utentiDiGuardia = utentiDiGuardia;
        this.utentiReperibili = utentiReperibili;
        this.tipologiaTurno = turno;
        this.servizio = servizio;
    }

    public AssegnazioneTurnoDTO(Long id,Long idTurno, long inizioEpoch, long fineEpoch, Set<UtenteDTO> utentiDiGuardia, Set<UtenteDTO> utentiReperibili, ServizioDTO servizio, TipologiaTurno turno, boolean reperibilitaAttiva) {
        this.id = id;
        this.inizioEpoch = inizioEpoch;
        this.fineEpoch = fineEpoch;
        this.utentiDiGuardia = utentiDiGuardia;
        this.utentiReperibili = utentiReperibili;
        this.tipologiaTurno = turno;
        this.servizio = servizio;
        this.idTurno=idTurno;
        this.reperibilitaAttiva = reperibilitaAttiva;
    }

    public AssegnazioneTurnoDTO(){}

    public AssegnazioneTurnoDTO(long inizioEpoch, long fineEpoch, Set<UtenteDTO> utentiDiGuardia, Set<UtenteDTO> utentiReperibili, ServizioDTO servizio, TipologiaTurno turno) {
        this.inizioEpoch = inizioEpoch;
        this.fineEpoch = fineEpoch;
        this.utentiDiGuardia = utentiDiGuardia;
        this.utentiReperibili = utentiReperibili;
        this.tipologiaTurno = turno;
        this.servizio = servizio;
    }

}
