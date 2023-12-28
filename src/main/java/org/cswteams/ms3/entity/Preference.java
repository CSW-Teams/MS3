package org.cswteams.ms3.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.cswteams.ms3.enums.TimeSlot;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;

/**
 * Represents a doctor's scheduling preference
 */
@Entity
@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Preference {

    @Id
    @GeneratedValue
    @EqualsAndHashCode.Include
    private Long id;

    private LocalDate date;
    @Column
    @Enumerated
    @ElementCollection(targetClass = TimeSlot.class)
    private List<TimeSlot> timeSlots;

    @ManyToMany
    private List<Doctor> doctors;

    /**
     *
     * @param date The day of the preference
     * @param timeSlots The shift time slots relative to the preference day
     * @param doctors A list of doctors that have such preference
     */
    public Preference(LocalDate date, List<TimeSlot> timeSlots, List<Doctor> doctors){
        this.date = date;
        this.timeSlots = timeSlots;
        this.doctors = doctors;
    }

    protected Preference(){

    }
}
