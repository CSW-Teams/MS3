package org.cswteams.ms3.entity;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
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

    /** tutti i turni assegnati a questo utente nella pianificazione corrente */
    List<AssegnazioneTurno> assegnazioniTurnoCache;

    List<AssegnazioneTurno> getAssegnazioniTurno(){
        
        if (assegnazioniTurnoCache == null){
            this.assegnazioniTurnoCache = new ArrayList<>();
            for (AssegnazioneTurno at: schedule.getAssegnazioniTurno()){
                for (Utente collega : at.getUtenti()){
                    if (collega.getId() == this.utente.getId()){
                        assegnazioniTurnoCache.add(at);
                        break;
                    }
                }
            }
        }
        return assegnazioniTurnoCache;
    }

    public UserScheduleState() {
    }
    
    public UserScheduleState(Utente utente, Schedule schedule) {
        this.utente = utente;
        this.schedule = schedule;
    }
}
