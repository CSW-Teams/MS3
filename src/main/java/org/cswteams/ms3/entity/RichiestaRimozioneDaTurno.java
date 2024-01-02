package org.cswteams.ms3.entity;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * Questa classe modella richieste di rimozione da un turno da parte di utenti a essi assegnati.
 * Gli utenti richiedenti possono fornire una motivazione/descrizione tramite l'apposito attributo.
 */
@Entity
@Getter
@Setter
public class RichiestaRimozioneDaTurno {
    @Id
    @Getter
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long idRichiestaRimozioneDaTurno;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "assegnazioneturno_id")
    private AssegnazioneTurno assegnazioneTurno;

    /**
     * Utente richiedente la sostituzione.
     */
    @NotNull
    @OneToOne
    private Utente utenteRichiedente;

    /**
     * Utente che, in caso di accettazione della richiesta, andrà a sostituire il richiedente.
     */
    @OneToOne
    private Utente utenteSostituto;

    /**
     * Eventuale descrizione della motivazione della richiesta.
     */
    @NotNull
    @NotEmpty
    private String descrizione;

    /**
     * Inizializzata a <code>false</code> (i.e. <i>"pending"</i>), viene settata a <code>true</code> quando il <i>Pianificatore</i>
     * prende una decisione in merito alla richiesta (approvazione o rigetto).
     */
    @NotNull
    private boolean esaminata;

    /**
     * Inizializzate a <code>false</code>, viene settata a <code>true</code> se il <i>Pianificatore</i>
     * accetta la richiesta, rimane a <code>false</code> in caso di rigetto.
     * Tale valore viene preso in considerazione solo se <code>esaminata</code> è settato a <code>true</code>.
     */
    @NotNull
    private boolean esito;

    /**
     * File che l'utente richiedente la rimozione può (facoltativamente) allegare,
     * e.g. certificato attestante condizione di malattia.
     */
    @Lob
    private byte[] allegato;

    public RichiestaRimozioneDaTurno() {
    }

    public RichiestaRimozioneDaTurno(@NotNull AssegnazioneTurno assegnazioneTurno, @NotNull Utente utenteRichiedente, String descrizione) {
        this.assegnazioneTurno = assegnazioneTurno;
        this.utenteRichiedente = utenteRichiedente;
        this.descrizione = descrizione;
        this.esaminata = false;
        this.esito = false;
        this.allegato = null;
    }
}
