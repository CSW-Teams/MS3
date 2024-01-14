package org.cswteams.ms3.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import org.cswteams.ms3.control.notification.Notificable;
import org.cswteams.ms3.control.notification.Observer;
import org.cswteams.ms3.enums.RequestStatus;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;

@Entity
@Data
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
//@Table(uniqueConstraints = { @UniqueConstraint(columnNames = { "sender_id", "shift_id", "receiver_id" }) })
public class Request extends Notificable {
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

    @Deprecated
    private Request() {
        super(null);
    }

    /*
        questa funzione crea una notifica che in base allo stato può cambiare il destinatario della notifica

     */
    @Override
    public Notification getNotification() {
        User user= null;
        String msg= "";
        switch (this.status){
            case ACCEPTED:
                msg = this.receiver.getName() + " " + this.receiver.getLastname() + " Ha accettato la tua richiesta di scambio turno";
                user = this.sender;
                break;
            case PENDING:
                msg = this.sender.getName() + " " + this.sender.getLastname() + " ti ha inviato una richiesta di scambio turno";
                user = this.receiver;
                break;
            case REFUSED:
                msg = this.receiver.getName() + " " + this.receiver.getLastname() + " Ha rifiutato la tua richiesta di scambio turno";
                user = this.receiver;
                break;
        }
        Notification notification=new Notification(user,msg);
        return notification;
    }

    public Request(User sender, User receiver, ConcreteShift turn, Observer observer) {
        super(observer);
        this.sender = sender;
        this.receiver = receiver;
        this.turn = turn;
        this.Notify();
    }

    public void setStatus(RequestStatus status) {
        this.status = status;
        this.Notify();
    }
}
