package org.cswteams.ms3.entity;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.*;

import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
/*
TODO: Check why there is this constraint
@Table(uniqueConstraints={
    @UniqueConstraint(columnNames={
        "doctor_id",
        "schedule_id",
    })
})
*/
public class DoctorScheduleState {

    @Id
    @GeneratedValue
    private Long id;

    /**  Utente a cui appartiene questo stato */
    @ManyToOne
    private Doctor doctor;

    /**  Pianificazione a cui appartiene questo stato */
    @OneToOne
    private Schedule schedule;

    private int uffaParziale=0;
    private int uffaCumulativo=0;

    /** tutti i turni assegnati a questo utente nella pianificazione corrente */
    @Transient
    List<ConcreteShift> assegnazioniTurnoCache;


    //funzione che ritorna la lista di ConcrateShift in un assegnazione corrente
    public List<ConcreteShift> getAssegnazioniTurnoCache(){

        if (assegnazioniTurnoCache == null){
            this.assegnazioniTurnoCache = new ArrayList<>();
            for (ConcreteShift concreteShift: schedule.getConcreteShifts()){
                for (DoctorAssignment da : concreteShift.getDoctorAssignmentList()) {
                    if (da.getDoctor().getId() == this.doctor.getId()){
                        assegnazioniTurnoCache.add(concreteShift);
                        break;
                    }
                }
            }
        }
        return assegnazioniTurnoCache;

    }

    /**Aggiunge in ordine la nuova assegnazione alla lista delle assegnazioni dell'utente **/
    public void addConcreteShift(ConcreteShift newConcreteShift){
        List<ConcreteShift> concreteShiftList = getAssegnazioniTurnoCache();
        int idInsert = concreteShiftList.size();
        for(int i = 0; i < concreteShiftList.size(); i++){
            if(concreteShiftList.get(i).getDate() > newConcreteShift.getDate() || concreteShiftList.get(i).getDate() == newConcreteShift.getDate()){
                if(concreteShiftList.get(i).getShift().getStartTime().isAfter(newConcreteShift.getShift().getStartTime())) {
                    idInsert = i;
                }
            }
        }
        concreteShiftList.add(idInsert,newConcreteShift);
    }

    public void saveUffaTemp(){
        this.uffaCumulativo = this.uffaParziale;
    }

    public void addUffaTemp(int uffa){
        this.uffaParziale =this.uffaCumulativo+ uffa;
    }

    public DoctorScheduleState() {
    }

    public DoctorScheduleState(Doctor doctor, Schedule schedule) {

        this.doctor = doctor;
        this.schedule = schedule;
    }
}