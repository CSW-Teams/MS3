package org.cswteams.ms3.dto;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
public class RequestRemovalFromConcreteShiftDTO {
    /**
     * <code>null</code> all'atto della creazione della richiesta
     */
    private Long idRichiestaRimozioneDaTurno;

    @NotNull
    private Long idAssegnazioneTurno;

    @NotNull
    private Long idUtenteRichiedente;

    private Long idUtenteSostituto;

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

    public RequestRemovalFromConcreteShiftDTO() {
    }

    public RequestRemovalFromConcreteShiftDTO(Long richiestaRimozioneDaTurnoId, Long IdAssegnazioneTurno, Long idUtenteRichiedente, boolean esito, String descrizione, byte[] allegato, boolean esaminata) {
        this.idRichiestaRimozioneDaTurno = richiestaRimozioneDaTurnoId;
        this.idAssegnazioneTurno = IdAssegnazioneTurno;
        this.idUtenteRichiedente = idUtenteRichiedente;
        this.esito = esito;
        this.descrizione = descrizione;
        this.allegato = allegato;
        this.esaminata = esaminata;
    }

    public RequestRemovalFromConcreteShiftDTO(Long richiestaRimozioneDaTurnoId, Long IdAssegnazioneTurno, Long idUtenteRichiedente, Long idUtenteSostituto, boolean esito, String descrizione, byte[] allegato, boolean esaminata) {
        this.idRichiestaRimozioneDaTurno = richiestaRimozioneDaTurnoId;
        this.idAssegnazioneTurno = IdAssegnazioneTurno;
        this.idUtenteRichiedente = idUtenteRichiedente;
        this.idUtenteSostituto = idUtenteSostituto;
        this.esito = esito;
        this.descrizione = descrizione;
        this.allegato = allegato;
        this.esaminata = esaminata;
    }
}
