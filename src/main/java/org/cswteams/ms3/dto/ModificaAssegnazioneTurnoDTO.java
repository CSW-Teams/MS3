package org.cswteams.ms3.dto;

import lombok.Data;

@Data
public class ModificaAssegnazioneTurnoDTO {
    long idAssegnazione;
    long[] utenti_guardia;
    long[] utenti_reperibili;

    long utenteModificatoreId;
}
