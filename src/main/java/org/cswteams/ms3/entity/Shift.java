package org.cswteams.ms3.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.cswteams.ms3.entity.constraint.AdditionalConstraint;
import org.cswteams.ms3.enums.TimeSlot;

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
    private List<DayOfWeek> daysOfWeek;


    @ManyToMany
    private List<MedicalService> medicalServices;


    @ManyToMany(cascade = CascadeType.ALL)
    private List<QuantityShiftSeniority> quantityShiftSeniority; // TODO: Refactor this class into an hashmap

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
                 List<QuantityShiftSeniority> quantityShiftSeniority, List<DayOfWeek> daysOfWeek,
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
     * Calcola il numero di utenti necessari per il turno sommando
     * il numero di utenti richiesto per ogni ruolo.
     * @return numero di utenti necessari per il turno.
     */
    public int getNumRequiredDoctors(){

        int numDoctors = 0;
        for(QuantityShiftSeniority quantityShiftSeniority : quantityShiftSeniority){
            numDoctors += quantityShiftSeniority.getQuantity();
        }
        return numDoctors;
    }

    protected Shift(){

    }

}
