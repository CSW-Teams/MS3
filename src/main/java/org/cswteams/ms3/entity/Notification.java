package org.cswteams.ms3.entity;

import lombok.Getter;
import lombok.Setter;
import org.cswteams.ms3.control.notification.Notificable;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Getter
@Setter
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "notification_id", nullable = false)
    private Long id;
    @ManyToOne
    @NotNull
    private User user;
    @NotNull
    private String message;

    @NotNull
    private boolean status;

    protected Notification(){};
    public Notification(User user,String message){
        this.user=user;
        this.message=message;
    }

}
