package org.cswteams.ms3.dto;


import lombok.Data;

@Data
public class GiustificazioneForzaturaVincoliDTO {

    private String message;

    private long utenteGiustificatoreId;

    //private Set<Liberatoria> liberatorie;

    public GiustificazioneForzaturaVincoliDTO() {

    }
    public GiustificazioneForzaturaVincoliDTO(String message, long utenteGiustificante) {
        this.message=message;
        this.utenteGiustificatoreId=utenteGiustificante;
        //this.liberatorie=liberatorie;
    }

}
