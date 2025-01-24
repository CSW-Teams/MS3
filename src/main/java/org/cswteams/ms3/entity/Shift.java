package org.cswteams.ms3.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.cswteams.ms3.entity.constraint.AdditionalConstraint;
import org.cswteams.ms3.enums.TaskEnum;
import org.cswteams.ms3.enums.TimeSlot;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.ParamDef;


import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalTime;
import java.util.*;

/**
 * An "abstract" <i>shift</i> (i.e., the raw time slot, planned and recurring by specific weekdays,
 * that will be assigned to some <code>Doctor</code>s after schedule generation), that can be istantiated
 * into schedules via <code>ConcreteShift</code>.
 */
@Entity
@Data
@EqualsAndHashCode
@Filter(
        name = "softDeleteFilter",
        condition = "exists (select 1 from medical_service ms where ms.medical_service_id = medical_service_medical_service_id and ms.deleted = :deleted)"
)
public class Shift {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "shift_id", nullable = false)
    private Long id;

    /**
     * The time slot for which this <i>shift</i> is associated.
     */
    @NotNull
    private TimeSlot timeSlot;

    /**
     * <i>Shift</i> start time.
     */
    @NotNull
    private LocalTime startTime;

    /**
     * <i>Shift</i> duration.
     * (e.g., the <i>shift</i> could cover more than one calendar day)
     */
    @NotNull
    private Duration duration;

    /**
     * On which days of the week can this shift be assigned.
     */
    @Enumerated
    @ElementCollection(targetClass = DayOfWeek.class)
    private Set<DayOfWeek> daysOfWeek;

    /**
     * <i>Medical Service</i> for the <i>shift</i>.
     */
    @ManyToOne
    private MedicalService medicalService;

    /**
     * <i>Doctors</i> required for this shift, grouped by <i>seniority</i>.
     */
    @OneToMany(cascade = CascadeType.ALL)
    private List<QuantityShiftSeniority> quantityShiftSeniority;

    /**
     * Additional <i>constraints</i> for the shift, if required.
     */
    @ManyToMany
    private List<AdditionalConstraint> additionalConstraints;

    /**
     * Soft delete flag to mark shifts as deleted without removing them from the database.
     */
    @Column(nullable = false)
    private boolean deleted = false;

    /**
     * Abstract concept of shift, created by the configurator
     *
     * @param StartTime              hh:mm:ss when the shift will start
     * @param duration               Duration of the shift in hh:mm:ss
     * @param medicalService         The medicalService to be provided in a shift
     * @param timeSlot               Moment of the day in which the shift will take place (morning, afternoon, night)
     * @param quantityShiftSeniority Quantity of doctors needed in the shift for each type of seniority
     * @param daysOfWeek             List of days in which this shift will take place
     * @param additionalConstraints  List of additional constraints which are specific of a shift (E.g. No over 62, for a risky operation)
     */
    public Shift(LocalTime StartTime, Duration duration, MedicalService medicalService, TimeSlot timeSlot,
                 List<QuantityShiftSeniority> quantityShiftSeniority, Set<DayOfWeek> daysOfWeek,
                 List<AdditionalConstraint> additionalConstraints) {
        this.startTime = StartTime;
        this.duration = duration;
        this.medicalService = medicalService;
        this.timeSlot = timeSlot;
        this.daysOfWeek = daysOfWeek;
        //TODO:Check correct data value between this and medicalService for more detail conctact me
        this.quantityShiftSeniority = quantityShiftSeniority;
        this.additionalConstraints = additionalConstraints;
        this.deleted = false; // Default value for a newly created shift
    }

    /**
     * Abstract concept of shift, created by the configurator <br/>
     * This constructor is useful for
     *
     * @param id                     The id of the shift
     * @param startTime              hh:mm:ss when the shift will start
     * @param duration               Duration of the shift in hh:mm:ss
     * @param medicalService         The medicalService to be provided in a shift
     * @param timeSlot               Moment of the day in which the shift will take place (morning, afternoon, night)
     * @param quantityShiftSeniority Quantity of doctors needed in the shift for each type of seniority and each task in the medicalService
     * @param daysOfWeek             List of days in which this shift will take place
     * @param additionalConstraints  List of additional constraints which are specific of a shift (E.g. No over 62, for a risky operation)
     */
    public Shift(Long id, TimeSlot timeSlot, LocalTime startTime, Duration duration,
                 Set<DayOfWeek> daysOfWeek, MedicalService medicalService,
                 List<QuantityShiftSeniority> quantityShiftSeniority,
                 List<AdditionalConstraint> additionalConstraints) {
        this.id = id;
        this.timeSlot = timeSlot;
        this.startTime = startTime;
        this.duration = duration;
        this.daysOfWeek = daysOfWeek;
        this.medicalService = medicalService;
        this.quantityShiftSeniority = quantityShiftSeniority;
        this.additionalConstraints = additionalConstraints;
        this.deleted = false; // Default value for a newly created shift
        if (!verifyCorrectnessQuantityShiftSeniority()) {
            throw new RuntimeException(); //TODO: inserire un eccezzione più logica
        }
    }

    /**
     * Check the correctness of the <i>Doctor</i>/<i>seniority</i> <i>shift</i> association.
     * @return
     */
    private boolean verifyCorrectnessQuantityShiftSeniority() {
        //per ora verifico solo che ci siano tutti i task
        List<Task> listTask = this.medicalService.getTasks();
        Map<TaskEnum, Integer> hashmap = new HashMap<>();
        for (Task t : listTask) {
            int count = 0;
            for (QuantityShiftSeniority q : this.quantityShiftSeniority) {
                if (q.getTask().getTaskType() == t.getTaskType()) {
                    count = count + 1;
                }
                hashmap.put(t.getTaskType(), count);
            }
        }
        for (Map.Entry<TaskEnum, Integer> entry : hashmap.entrySet()) {
            if (entry.getValue() == 0)
                return false;
        }
        return true;
    }

    /**
     * Default constructor needed by Lombok
     */
    protected Shift() {

    }

}
