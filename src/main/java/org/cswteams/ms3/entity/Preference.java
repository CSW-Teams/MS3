package org.cswteams.ms3.entity;

import lombok.Getter;
import org.cswteams.ms3.enums.TimeSlot;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;

@Entity
@Getter
public class Preference {

    @Id
    @GeneratedValue
    private Long id;

    private LocalDate date;
    @Column
    @Enumerated
    @ElementCollection(targetClass = TimeSlot.class)
    private List<TimeSlot> timeSlots;

    @ManyToMany
    private List<Doctor> doctors;

    public Preference(LocalDate date, List<TimeSlot> timeSlots, List<Doctor> doctors){
        this.date = date;
        this.timeSlots = timeSlots;
        this.doctors = doctors;
    }

    public Preference(){

    }
}
