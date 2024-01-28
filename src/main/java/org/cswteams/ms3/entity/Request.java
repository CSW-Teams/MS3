package org.cswteams.ms3.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.cswteams.ms3.control.notification.Notificable;
import org.cswteams.ms3.control.notification.Observer;
import org.cswteams.ms3.enums.RequestStatus;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

/**
 * A request for <i>Concrete Shift</i> exchange.
 */
@Entity
@Data
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@EqualsAndHashCode(callSuper = true)
//@Table(uniqueConstraints = { @UniqueConstraint(columnNames = { "sender_id", "shift_id", "receiver_id" }) })
public class Request extends Notificable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Long id;

    /**
     * Issuing user.
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @NotNull
    private User sender;

    /**
     * Recipient user.
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @NotNull
    private User receiver;

    /**
     * <i>Concrete shift</i> to be updated
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @NotNull
    private ConcreteShift turn;

    private RequestStatus status = RequestStatus.PENDING;

    /**
     * Default constructor needed by Lombok
     */
    protected Request() {
        super(null);
    }

    /**
     * {@inheritDoc}
     * This function creates a notification which, based on the status, can change the recipient of the notification.
     * @return a new notification for the shift change request
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

    /**
     * Set the status of the request to <code>status</code>.
     * @param status new status for the request
     */
    public void setStatus(RequestStatus status) {
        this.status = status;
        this.Notify();
    }
}
