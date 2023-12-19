package org.cswteams.ms3.entity;


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
public class RequestRemovalFromConcreteShift {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "request_removal_from_concrete_shift_id", nullable = false)
    private Long id;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "concrete_shift_id")
    private ConcreteShift concreteShift;

    /**
     * Utente richiedente la sostituzione.
     */
    @NotNull
    @OneToOne
    private Doctor requestingDoctor;

    /**
     * Utente che, in caso di accettazione della richiesta, andrà a sostituire il richiedente.
     */
    @OneToOne
    @NotNull
    private Doctor substituteDoctor;

    /**
     * Eventuale descrizione della motivazione della richiesta.
     */
    @NotNull
    @NotEmpty
    private String reason;

    /**
     * Inizializzata a <code>false</code> (i.e. <i>"pending"</i>), viene settata a <code>true</code> quando il <i>Pianificatore</i>
     * prende una decisione in merito alla richiesta (approvazione o rigetto).
     */
    @NotNull
    private boolean isReviewed;

    /**
     * Inizializzate a <code>false</code>, viene settata a <code>true</code> se il <i>Pianificatore</i>
     * accetta la richiesta, rimane a <code>false</code> in caso di rigetto.
     * Tale valore viene preso in considerazione solo se <code>esaminata</code> è settato a <code>true</code>.
     */
    @NotNull
    private boolean isAccepted;

    /**
     * File che l'utente richiedente la rimozione può (facoltativamente) allegare,
     * e.g. certificato attestante condizione di malattia.
     */
    @Lob
    private byte[] file;

    protected RequestRemovalFromConcreteShift() {
    }

    public RequestRemovalFromConcreteShift(ConcreteShift concreteShift, Doctor requestingDoctor, String reason) {
        this.concreteShift = concreteShift;
        this.requestingDoctor = requestingDoctor;
        this.reason = reason;
        this.isReviewed = false;
        this.isAccepted = false;
        this.file = null;
    }


}
