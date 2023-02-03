package org.cswteams.ms3.dto;

import lombok.Data;
import org.cswteams.ms3.enums.MansioneEnum;
import org.cswteams.ms3.enums.TipologiaTurno;
import java.util.Set;

@Data
public class RegistraAssegnazioneTurnoDTO {

    private int giorno;
    private int mese;
    private int anno;

    private TipologiaTurno tipologiaTurno;

    private MansioneEnum mansione;
    private Set<UtenteDTO> utentiDiGuardia;
    private Set<UtenteDTO> utentiReperibili;
    private ServizioDTO servizio;
    private boolean forced;

    public RegistraAssegnazioneTurnoDTO(){}

    public void setForced(boolean forced) {
        this.forced = forced;
    }


}
