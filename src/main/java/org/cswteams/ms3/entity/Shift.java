package org.cswteams.ms3.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.cswteams.ms3.entity.constraint.AdditionalConstraint;
import org.cswteams.ms3.enums.TimeSlot;
import org.hibernate.mapping.KeyValue;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalTime;
import java.util.*;

@Entity
@Data
@EqualsAndHashCode
public class Shift {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "shift_id", nullable = false)
    private Long id;

    @NotNull
    private TimeSlot timeSlot;

    @NotNull
    private LocalTime startTime;

    @NotNull
    private Duration duration;

    /**
     * In quali giorni della settimana questo turno può essere assegnato
     */
    @Enumerated
    @ElementCollection(targetClass = DayOfWeek.class)
    private Set<DayOfWeek> daysOfWeek;


    @ManyToMany
    private List<MedicalService> medicalServices;

    @Lob
    private HashMap<Seniority, Integer> quantityShiftSeniority;

    @ManyToMany
    private List<AdditionalConstraint> additionalConstraints;

    /**
     * Abstract concept of shift, created by the configurator
     * @param StartTime hh:mm:ss when the shift will start
     * @param duration Duration of the shift in hh:mm:ss
     * @param medicalServices List of medicalServices to be provided in a shift
     * @param timeSlot Moment of the day in which the shift will take place (morning, afternoon, night)
     * @param quantityShiftSeniority Quantity of doctors needed in the shift for each type of seniority
     * @param daysOfWeek List of days in which this shift will take place
     * @param additionalConstraints List of additional constraints which are specific of a shift (E.g. No over 62, for a risky operation)
     */
    public Shift(LocalTime StartTime, Duration duration, List<MedicalService> medicalServices, TimeSlot timeSlot,
                 HashMap<Seniority, Integer> quantityShiftSeniority, Set<DayOfWeek> daysOfWeek,
                 List<AdditionalConstraint> additionalConstraints) {
        this.startTime = StartTime;
        this.duration = duration;
        this.medicalServices = medicalServices;
        this.timeSlot = timeSlot;
        this.daysOfWeek = daysOfWeek;
        this.quantityShiftSeniority = quantityShiftSeniority;
        this.additionalConstraints = additionalConstraints;
    }

    /**
     * Abstract concept of shift, created by the configurator <br/>
     * This constructor is useful for
     * @param id The id of the shift
     * @param startTime hh:mm:ss when the shift will start
     * @param duration Duration of the shift in hh:mm:ss
     * @param medicalServices List of medicalServices to be provided in a shift
     * @param timeSlot Moment of the day in which the shift will take place (morning, afternoon, night)
     * @param quantityShiftSeniority Quantity of doctors needed in the shift for each type of seniority
     * @param daysOfWeek List of days in which this shift will take place
     * @param additionalConstraints List of additional constraints which are specific of a shift (E.g. No over 62, for a risky operation)
     */
    public Shift(Long id, TimeSlot timeSlot, LocalTime startTime, Duration duration,
                 Set<DayOfWeek> daysOfWeek, List<MedicalService> medicalServices,
                 HashMap<Seniority, Integer> quantityShiftSeniority,
                 List<AdditionalConstraint> additionalConstraints) {
        this.id = id;
        this.timeSlot = timeSlot;
        this.startTime = startTime;
        this.duration = duration;
        this.daysOfWeek = daysOfWeek;
        this.medicalServices = medicalServices;
        this.quantityShiftSeniority = quantityShiftSeniority;
        this.additionalConstraints = additionalConstraints;
    }

    /**
     * Calcola il numero di utenti necessari per il turno sommando
     * il numero di utenti richiesto per ogni ruolo.
     * @return numero di utenti necessari per il turno.
     */
    public int getNumRequiredDoctors(){

        int numDoctors = 0;
        for(Integer i : quantityShiftSeniority.values()){
            numDoctors += i;
        }
        return numDoctors;
    }

    protected Shift(){

    }

}
