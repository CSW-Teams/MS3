package org.cswteams.ms3.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

/**
 * This entity represents an element of the notification system.
 */
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
    private TenantUser user;

    @NotNull
    private String message;

    @NotNull
    private boolean status;

    /**
     * Default constructor needed by Lombok
     */
    protected Notification() {
    }

    /**
     * Create a new notification for the specific <i>TenantUser</i>, with a specific text.
     *
     * @param user    <i>TenantUser</i> to be notified
     * @param message message to be delivered
     */
    public Notification(TenantUser user, String message) {
        this.user=user;
        this.message=message;
    }

}
