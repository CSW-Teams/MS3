package org.cswteams.ms3.entity;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.*;

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
    @Transient
    List<AssegnazioneTurno> assegnazioniTurnoCache;

    public List<AssegnazioneTurno> getAssegnazioniTurnoCache(){
        
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

    /**Aggiunge in ordine la nuova assegnazione alla lista delle assegnazioni dell'utente **/
    public void addAssegnazioneTurno(AssegnazioneTurno nuovaAssegnazione){
        List<AssegnazioneTurno> turniAssegnati = getAssegnazioniTurnoCache();
        int idInsert = turniAssegnati.size();
        for(int i = 0; i < turniAssegnati.size(); i++){
            if(turniAssegnati.get(i).getData().isAfter(nuovaAssegnazione.getData()) || turniAssegnati.get(i).getData().isEqual(nuovaAssegnazione.getData())){
                if(turniAssegnati.get(i).getTurno().getOraInizio().isAfter(nuovaAssegnazione.getTurno().getOraInizio())) {
                    idInsert = i;
                }
            }
        }
        turniAssegnati.add(idInsert,nuovaAssegnazione);
    }

    public UserScheduleState() {
    }
    
    public UserScheduleState(Utente utente, Schedule schedule) {
        this.utente = utente;
        this.schedule = schedule;
    }
}
