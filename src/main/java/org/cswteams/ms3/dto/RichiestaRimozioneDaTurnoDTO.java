package org.cswteams.ms3.dto;

import lombok.Data;
import org.cswteams.ms3.entity.CategoriaUtente;
import org.cswteams.ms3.enums.RuoloEnum;

import java.util.List;

@Data
public class RichiestaRimozioneDaTurnoDTO {
    private Long assegnazioneTurnoId;

    private String nome;
    private String cognome;
    private String dataNascita;
    private RuoloEnum ruoloEnum;
    private List<CategoriaUtente> stato;
    private List<CategoriaUtente> specializzazioni;

    String descrizione;

    public RichiestaRimozioneDaTurnoDTO() {
    }

    public RichiestaRimozioneDaTurnoDTO(Long assegnazioneTurnoId,
                                        String nome,
                                        String cognome,
                                        String dataNascita,
                                        RuoloEnum ruoloEnum,
                                        List<CategoriaUtente> stato,
                                        List<CategoriaUtente> specializzazioni,
                                        String descrizione) {
        this.assegnazioneTurnoId = assegnazioneTurnoId;
        this.nome = nome;
        this.cognome = cognome;
        this.dataNascita = dataNascita;
        this.ruoloEnum = ruoloEnum;
        this.stato = stato;
        this.specializzazioni = specializzazioni;
        this.descrizione = descrizione;
    }
}
