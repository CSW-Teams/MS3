package org.cswteams.ms3.dto;


import lombok.Data;
import org.cswteams.ms3.entity.Files;
import org.cswteams.ms3.entity.Utente;

import java.util.Set;

@Data
public class GiustificazioneForzaturaDto {

    private String message;

    private Utente utenteGiustificante;

    private Set<Files> delibere;

    public GiustificazioneForzaturaDto() {

    }
    public GiustificazioneForzaturaDto(String message,Utente utenteGiustificante,Set<Files> delibere) {
        this.message=message;
        this.utenteGiustificante=utenteGiustificante;
        this.delibere=delibere;
    }
}
