package org.cswteams.ms3.dto;


import lombok.Data;
import org.cswteams.ms3.entity.Turno;
import org.cswteams.ms3.entity.Utente;
import org.cswteams.ms3.enums.TipologiaTurno;

import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Data
public class GiustificazioneForzaturaVincoliDTO {

    private String message;

    private String utenteGiustificatoreId;

    private int giorno;
    private int mese;
    private int anno;

    private TipologiaTurno tipologiaTurno;
    private Set<UtenteDTO> utentiAllocati;
    private ServizioDTO servizio;


    //private Set<Liberatoria> liberatorie;

    public GiustificazioneForzaturaVincoliDTO() {

    }

}
