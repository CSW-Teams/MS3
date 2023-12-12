package org.cswteams.ms3.dto;


import lombok.Data;
import org.cswteams.ms3.enums.TipologiaTurno;

import java.util.Set;

@Data
public class GiustificazioneForzaturaVincoliDTO {

    private String message;

    private String utenteGiustificatoreId;

    private int giorno;
    private int mese;
    private int anno;

    private TipologiaTurno tipologiaTurno;
    private Set<DoctorDTO> utentiAllocati;
    private ServizioDTO servizio;


    //private Set<Liberatoria> liberatorie;

    public GiustificazioneForzaturaVincoliDTO() {

    }

}
