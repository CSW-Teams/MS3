package org.cswteams.ms3.dto;


import lombok.Data;
import org.cswteams.ms3.enums.TipologiaTurno;

import java.time.LocalDate;
import java.util.Set;

@Data
public class GiustificazioneForzaturaVincoliDTO {

    private String message;

    private String utenteGiustificatoreId;

    private LocalDate giorno;

    private TipologiaTurno tipologiaTurno;
    private Set<DoctorDTO> utentiAllocati;
    private ServizioDTO servizio;


    //private Set<Liberatoria> liberatorie;

    public GiustificazioneForzaturaVincoliDTO() {

    }

}
