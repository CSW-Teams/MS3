package org.cswteams.ms3.entity;

import lombok.Data;
import org.cswteams.ms3.enums.TimeSlot;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Set;

/**
 * A justification for constraints forcing.
 */
@Data
@Entity
@Table(name = "giustificazione_forzatura_vincoli")
public class GiustificazioneForzaturaVincoli {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

  /*  @OneToMany(cascade = CascadeType.ALL)
    private Set<Waiver> liberatorie;*/

    @Column(name = "violating_shift")
    private TimeSlot violatingShift;

    private LocalDate date;

    @ManyToMany
    @JoinTable(
            name = "giustificazione_forzatura_vincoli_assigned_doctors",
            joinColumns = @JoinColumn(name = "giustificazione_forzatura_vincoli_id"),
            inverseJoinColumns = @JoinColumn(name = "assigned_doctors_ms3_tenant_user_id")
    )
    private Set<Doctor> assignedDoctors;

    @ManyToOne
    @JoinColumn(name = "service_medical_service_id")
    private MedicalService service;

    /*@ManyToMany
    private List<Constraint> vincoliViolati;*/
    private String reason;

    @ManyToOne
    @JoinColumn(name = "justifying_doctor_ms3_tenant_user_id")
    private Doctor justifyingDoctor;

    /**
     * Default constructor needed by Lombok
     */
    public GiustificazioneForzaturaVincoli() {

    }

    public GiustificazioneForzaturaVincoli(String message, TimeSlot violatingShift, MedicalService service, LocalDate date, Set<Doctor> assignedDoctors, Doctor justifyingDoctor) {
        this.violatingShift = violatingShift;
        this.date = date;
        this.assignedDoctors = assignedDoctors;
        this.service = service;
        this.reason = message;
        this.justifyingDoctor = justifyingDoctor;
    }
}
