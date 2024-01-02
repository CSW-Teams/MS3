package org.cswteams.ms3.dto;

import lombok.Data;
import org.cswteams.ms3.enums.TipologiaTurno;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
public class DesiderataDTO {

    private Long idDesiderata;
    private LocalDate giorno;
   private List<TipologiaTurno> tipologieTurni;

    public DesiderataDTO(LocalDate giorno, List<TipologiaTurno> tipologieTurni) {
        this.giorno = giorno;
        this.tipologieTurni = tipologieTurni;
    }


    public DesiderataDTO(LocalDate giorno) {
        this.giorno = giorno;
        this.tipologieTurni = new ArrayList<>();
    }

    public DesiderataDTO(Long idDesiderata, LocalDate giorno, List<TipologiaTurno> tipologieTurni) {
        this(giorno, tipologieTurni);
        this.idDesiderata = idDesiderata;
    }

    public DesiderataDTO(){}
}
