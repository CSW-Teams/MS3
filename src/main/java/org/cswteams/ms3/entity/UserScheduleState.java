package org.cswteams.ms3.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import lombok.Data;

@Entity
@Data
@Table(uniqueConstraints={
    @UniqueConstraint(columnNames={
        "utente_id",
        "schedule_id",
    })
})

public class UserScheduleState {
    
    @Id
    @GeneratedValue
    private Long id;
    
    /**  Utente a cui appartiene questo stato */
    @ManyToOne
    private Utente utente;

    /**  Pianificazione a cui appartiene questo stato */
    @OneToOne
    private Schedule schedule;

    /** Quante ore sono state pianificate per questo utente in questa pianificazione */
    private int scheduledHours;

    public UserScheduleState() {
    }
    
    public UserScheduleState(Utente utente, Schedule schedule) {
        this.utente = utente;
        this.schedule = schedule;
        this.scheduledHours = 0;
    }
}
