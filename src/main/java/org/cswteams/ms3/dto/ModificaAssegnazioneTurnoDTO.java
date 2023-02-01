package org.cswteams.ms3.dto;

import lombok.Data;

import java.util.List;

@Data
public class ModificaAssegnazioneTurnoDTO {
    long idAssegnazione;
    long[] utenti_guardia;
    long[] utenti_reperibili;

    public ModificaAssegnazioneTurnoDTO(long idAssegnazione, long[] utenti_guardia, long[] utenti_reperibili) {
        this.idAssegnazione = idAssegnazione;
        this.utenti_guardia = utenti_guardia;
        this.utenti_reperibili = utenti_reperibili;
    }

    public ModificaAssegnazioneTurnoDTO() {
    }

}
