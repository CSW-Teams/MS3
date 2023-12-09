package org.cswteams.ms3.dto;

import lombok.Data;
import org.cswteams.ms3.entity.CategoriaUtente;
import org.cswteams.ms3.enums.RuoloEnum;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class RichiestaRimozioneDaTurnoDTO {
    /**
     * <code>null</code> all'atto della creazione della richiesta
     */
    private Long id;

    @NotNull
    private Long assegnazioneTurnoId;

    @NotNull
    private Long utenteId;

    /**
     * Considerato solo all'atto della risoluzione della richiesta da parte del <i>Pianificatore</i>
     */
    @NotNull
    private boolean esito;

    @NotNull
    @NotEmpty
    String descrizione;

    //-----------------------------------------
    // TODO scremare gli attributi seguenti
    private String nome;
    private String cognome;
    private String dataNascita;
    private RuoloEnum ruoloEnum;
    private List<CategoriaUtente> stato;
    private List<CategoriaUtente> specializzazioni;


    public RichiestaRimozioneDaTurnoDTO() {
    }

    public RichiestaRimozioneDaTurnoDTO(Long assegnazioneTurnoId,
                                        Long utenteId,
                                        String nome,
                                        String cognome,
                                        String dataNascita,
                                        RuoloEnum ruoloEnum,
                                        List<CategoriaUtente> stato,
                                        List<CategoriaUtente> specializzazioni,
                                        String descrizione) {
        this.assegnazioneTurnoId = assegnazioneTurnoId;
        this.utenteId = utenteId;
        this.nome = nome;
        this.cognome = cognome;
        this.dataNascita = dataNascita;
        this.ruoloEnum = ruoloEnum;
        this.stato = stato;
        this.specializzazioni = specializzazioni;
        this.descrizione = descrizione;
    }
}
