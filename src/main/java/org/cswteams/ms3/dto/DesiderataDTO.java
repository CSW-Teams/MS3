package org.cswteams.ms3.dto;

import lombok.Data;
import org.cswteams.ms3.enums.TipologiaTurno;

import java.util.ArrayList;
import java.util.List;

@Data
public class DesiderataDTO {

    private Long idDesiderata;
    private int giorno;
    private int mese;
    private int anno;
   private List<TipologiaTurno> tipologieTurni;

    public DesiderataDTO(int giorno, int mese, int anno, List<TipologiaTurno> tipologieTurni) {
        this.giorno = giorno;
        this.mese = mese;
        this.anno = anno;
        this.tipologieTurni = tipologieTurni;
    }


    public DesiderataDTO(int giorno, int mese, int anno) {
        this.giorno = giorno;
        this.mese = mese;
        this.anno = anno;
        this.tipologieTurni = new ArrayList<>();
    }

    public DesiderataDTO(Long idDesiderata, int giorno, int mese, int anno, List<TipologiaTurno> tipologieTurni) {
        this(giorno, mese, anno, tipologieTurni);
        this.idDesiderata = idDesiderata;
    }

    public DesiderataDTO(){}
}
