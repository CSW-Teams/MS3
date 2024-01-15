package org.cswteams.ms3.entity;

import org.cswteams.ms3.enums.PriorityQueueEnum;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

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
public class DoctorUffaPriority {
    
    @Id
    @GeneratedValue
    private Long id;

    /** Doctor which the Uffa priority refers to */
    @OneToOne
    @NotNull
    private Doctor doctor;

    /** Current schedule */
    //@OneToOne
    //private Schedule schedule;
    private long idSchedule;

    private int partialGeneralPriority = 0;
    private int generalPriority = 0;
    private int partialLongShiftPriority = 0;
    private int longShiftPriority = 0;
    private int partialNightPriority = 0;
    private int nightPriority = 0;

    /** All the concrete shifts assigned to the doctor in the current schedule */
    @Transient
    List<ConcreteShift> assegnazioniTurnoCache;


    public DoctorUffaPriority() {
    }

    public DoctorUffaPriority(Doctor doctor) {

        this.doctor = doctor;

    }

    public DoctorUffaPriority(Doctor doctor, Schedule schedule) {

        this.doctor = doctor;
        this.schedule = schedule;
    }


    /** This method returns a ConcreteShift list for the current schedule. **/
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

    /** This method adds (in order) the new contrete shift to the concrete shifts list related to the referring doctor. **/
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

    public void updatePriority(PriorityQueueEnum pq) {  //saveUffaTemp() counterpart

        switch(pq) {
            case GENERAL:
                this.generalPriority = this.partialGeneralPriority;

            case LONG_SHIFT:
                this.longShiftPriority = this.partialLongShiftPriority;

            case NIGHT:
                this.nightPriority = this.partialNightPriority;

        }

    }

    public void updatePartialPriority(int priorityDelta, PriorityQueueEnum pq) {  //addUffaTemp() counterpart

        switch(pq) {
            case GENERAL:
                this.partialGeneralPriority = this.generalPriority + priorityDelta;

            case LONG_SHIFT:
                this.partialLongShiftPriority = this.longShiftPriority + priorityDelta;

            case NIGHT:
                this.partialNightPriority = this.nightPriority + priorityDelta;

        }

    }

}
