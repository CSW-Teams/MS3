package org.cswteams.ms3.entity;

import lombok.Data;
import lombok.Getter;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * Questa classe modella richieste di rimozione da un turno da parte di utenti a essi assegnati.
 * Gli utenti richiedenti possono fornire una motivazione/descrizione tramite l'apposito attributo.
 */
@Entity
@Data
public class RichiestaRimozioneDaTurno {
    @Id
    @Getter
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Long id;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "assegnazioneturno_id")
    private AssegnazioneTurno assegnazioneTurno;

    /**
     * Utente richiedente.
     */
    @NotNull
    @OneToOne
    private Utente utente;

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

    public RichiestaRimozioneDaTurno() {
    }

    public RichiestaRimozioneDaTurno(@NotNull AssegnazioneTurno assegnazioneTurno, @NotNull Utente utente, String descrizione) {
        this.assegnazioneTurno = assegnazioneTurno;
        this.utente = utente;
        this.descrizione = descrizione;
        this.esaminata = false;
        this.esito = false;
    }
}
