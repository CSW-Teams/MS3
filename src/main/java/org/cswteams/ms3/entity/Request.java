package org.cswteams.ms3.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import org.cswteams.ms3.enums.RequestStatus;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Data
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
//@Table(uniqueConstraints = { @UniqueConstraint(columnNames = { "sender_id", "shift_id", "receiver_id" }) })
public class Request {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Long id;

    /** Id utente richiedente */
    @ManyToOne(fetch = FetchType.EAGER)
    @NotNull
    private User sender;

    /** Id utente ricevente */
    @ManyToOne(fetch = FetchType.EAGER)
    @NotNull
    private User receiver;

    /** Id turno da modificare */
    @ManyToOne(fetch = FetchType.EAGER)
    @NotNull
    private ConcreteShift turn;

    private RequestStatus status = RequestStatus.PENDING;

    public Request() {

    }

    public Request(User sender, User receiver, ConcreteShift turn) {
        this.sender = sender;
        this.receiver = receiver;
        this.turn = turn;
    }

}
