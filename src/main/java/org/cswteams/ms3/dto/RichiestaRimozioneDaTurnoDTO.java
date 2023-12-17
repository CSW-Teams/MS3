package org.cswteams.ms3.dto;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

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
    private String descrizione;

    @NotNull
    private boolean esaminata;

    private byte[] allegato;

    public RichiestaRimozioneDaTurnoDTO() {
    }

    public RichiestaRimozioneDaTurnoDTO(Long richiestaRimozioneDaTurnoId, Long assegnazioneTurnoId, Long utenteId, boolean esito, String descrizione, byte[] allegato, boolean esaminata) {
        this.id = richiestaRimozioneDaTurnoId;
        this.assegnazioneTurnoId = assegnazioneTurnoId;
        this.utenteId = utenteId;
        this.esito = esito;
        this.descrizione = descrizione;
        this.allegato = allegato;
        this.esaminata = esaminata;
    }
}
