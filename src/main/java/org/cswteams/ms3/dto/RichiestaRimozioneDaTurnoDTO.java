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

    public RichiestaRimozioneDaTurnoDTO() {
    }

    public RichiestaRimozioneDaTurnoDTO(Long assegnazioneTurnoId, Long utenteId, boolean esito, String descrizione) {
        this.assegnazioneTurnoId = assegnazioneTurnoId;
        this.utenteId = utenteId;
        this.esito = esito;
        this.descrizione = descrizione;
    }
}
