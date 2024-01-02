package org.cswteams.ms3.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import org.cswteams.ms3.enums.RequestENUM;
import org.hibernate.validator.constraints.UniqueElements;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Data
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Table(uniqueConstraints = { @UniqueConstraint(columnNames = { "sender_id", "turn_id", "receiver_id" }) })
public class Request {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Long id;

    /** Id utente richiedente */
    @ManyToOne(fetch = FetchType.EAGER)
    @NotNull
    private Utente sender;

    /** Id utente ricevente */
    @ManyToOne(fetch = FetchType.EAGER)
    @NotNull
    private Utente receiver;

    /** Id turno da modificare */
    @ManyToOne(fetch = FetchType.EAGER)
    @NotNull
    private AssegnazioneTurno turn;

    private RequestENUM status = RequestENUM.PENDING;

    public Request() {

    }

    public Request(Utente sender, Utente receiver, AssegnazioneTurno turn) {
        this.sender = sender;
        this.receiver = receiver;
        this.turn = turn;
    }

}
