package org.cswteams.ms3.dto;


import lombok.Data;
import org.cswteams.ms3.entity.Liberatoria;
import org.cswteams.ms3.entity.Utente;

import java.util.Set;

@Data
public class GiustificazioneForzaturaDto {

    private String message;

    private Utente utenteGiustificante;

    private Set<Liberatoria> delibere;

    public GiustificazioneForzaturaDto() {

    }
    public GiustificazioneForzaturaDto(String message,Utente utenteGiustificante,Set<Liberatoria> delibere) {
        this.message=message;
        this.utenteGiustificante=utenteGiustificante;
        this.delibere=delibere;
    }
}
