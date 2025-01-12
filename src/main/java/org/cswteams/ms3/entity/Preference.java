package org.cswteams.ms3.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.cswteams.ms3.enums.TimeSlot;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

/**
 * Represents a doctor's scheduling preference
 *
 * @see <a href="https://github.com/CSW-Teams/MS3/wiki#desiderata">Glossary</a>.
 */
@Entity
@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Preference {

    @Id
    @GeneratedValue
    @EqualsAndHashCode.Include
    private Long id;

    /**
     * The day of the preference
     */
    private LocalDate date;

    /**
     * The shift time slots relative to the preference day
     */
    @Enumerated
    @ElementCollection(targetClass = TimeSlot.class)
    @CollectionTable(
            name = "preference_time_slots",
            joinColumns = @JoinColumn(name = "preference_id")
    )
    @Column(name = "time_slots")
    private Set<TimeSlot> timeSlots;

    /**
     * A list of doctors that have such preference
     */
    @ManyToMany
    @JoinTable(
            name = "preference_doctors",
            joinColumns = @JoinColumn(name = "preference_id"),
            inverseJoinColumns = @JoinColumn(name = "doctors_ms3_tenant_user_id")
    )
    private List<Doctor> doctors;

    /**
     * Create a new <i>Preference</i> with the specified parameters.
     *
     * @param date      The day of the preference
     * @param timeSlots The shift time slots relative to the preference day
     * @param doctors   A list of doctors that have such preference
     */
    public Preference(LocalDate date, Set<TimeSlot> timeSlots, List<Doctor> doctors){
        this.date = date;
        this.timeSlots = timeSlots;
        this.doctors = doctors;
    }

    /**
     * Create a new <i>Preference</i> with the specified parameters.
     *
     * @param id        The id of the preference
     * @param date      The day of the preference
     * @param timeSlots The shift time slots relative to the preference day
     * @param doctors   A list of doctors that have such preference
     */
    public Preference(Long id, LocalDate date, Set<TimeSlot> timeSlots, List<Doctor> doctors) {
        this.id = id;
        this.date = date;
        this.timeSlots = timeSlots;
        this.doctors = doctors;
    }

    /**
     * Default constructor needed by Lombok
     */
    protected Preference() {

    }
}
