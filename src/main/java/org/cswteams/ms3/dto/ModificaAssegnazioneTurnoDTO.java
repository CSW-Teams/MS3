package org.cswteams.ms3.dto;

import lombok.Data;

@Data
public class ModificaAssegnazioneTurnoDTO {
    long idAssegnazione;
    long[] utenti_guardia;
    long[] utenti_reperibili;

    long utenteModificatoreId;

    public ModificaAssegnazioneTurnoDTO(long idAssegnazione, long[] utenti_guardia, long[] utenti_reperibili, long utenteModificatoreId) {
        this.idAssegnazione = idAssegnazione;
        this.utenti_guardia = utenti_guardia;
        this.utenti_reperibili = utenti_reperibili;
        this.utenteModificatoreId = utenteModificatoreId;
    }

    public ModificaAssegnazioneTurnoDTO() {
    }

}
