package org.cswteams.ms3.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * This class models requests of removal from <i>concrete shifts</i>, issued by <i>doctors</i> assigned to them.
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
     * Doctor issuing the request.
     */
    @NotNull
    @OneToOne
    private Doctor requestingDoctor;

    /**
     * Doctor that the <i>Planner</i> will put as substitute for <code>requestingDoctor</code>, if the request will be accepted.
     */
    @OneToOne
    private Doctor substituteDoctor;

    /**
     * Description of the reason for the request.
     */
    @NotNull
    @NotEmpty
    private String reason;

    /**
     * Initially set to <code>false</code> (i.e. <i>"pending"</i>), will be set to <code>true</code> when the <i>Planner</i>
     * will review the request.
     */
    @NotNull
    private boolean isReviewed;

    /**
     * Initially set to <code>false</code> (i.e. <i>"pending"</i>), will be set to <code>true</code> if the <i>Planner</i>
     * will accept the request, otherwise it will remain set to <code>false</code>.
     * This value is only to be considered if <code>isReviewed</code> is set to <code>true</code>.
     */
    @NotNull
    private boolean isAccepted;

    /**
     * File that the doctor issuing the request could (not mandatory) upload,
     * e.g. certificate certifying illness condition.
     */
    @Lob
    private byte[] file;

    /**
     * Default constructor needed by Lombok
     */
    protected RequestRemovalFromConcreteShift() {
    }

    /**
     * Create a new request of removal from <i>concrete shift</i>, with the specified parameters
     *
     * @param concreteShift    the <i>concrete shift</i> this request is related to
     * @param requestingDoctor the <i>Doctor</i> issuing this request
     * @param reason           a brief description for the reason of this request
     */
    public RequestRemovalFromConcreteShift(ConcreteShift concreteShift, Doctor requestingDoctor, String reason) {
        this.concreteShift = concreteShift;
        this.requestingDoctor = requestingDoctor;
        this.reason = reason;
        this.isReviewed = false;
        this.isAccepted = false;
        this.file = null;
    }
}
