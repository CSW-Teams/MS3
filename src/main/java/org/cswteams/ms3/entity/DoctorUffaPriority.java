package org.cswteams.ms3.entity;

import org.cswteams.ms3.enums.PriorityQueueEnum;

import java.lang.Math;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;

/**
 * All the information about priority levels on the queues of the doctors.
 */
@Entity
@Table(name = "doctor_uffa_priority")
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
    @JoinColumn(name = "doctor_ms3_tenant_user_id", referencedColumnName = "ms3_tenant_user_id")
    private Doctor doctor;

    /** Current schedule */
    @OneToOne
    @JoinColumn(name = "schedule_schedule_id", referencedColumnName = "schedule_id")
    private Schedule schedule;

    @Column(name = "partial_general_priority")
    private int partialGeneralPriority = 0;

    @Column(name = "general_priority")
    private int generalPriority = 0;

    @Column(name = "partial_long_shift_priority")
    private int partialLongShiftPriority = 0;

    @Column(name = "long_shift_priority")
    private int longShiftPriority = 0;

    @Column(name = "partial_night_priority")
    private int partialNightPriority = 0;

    @Column(name = "night_priority")
    private int nightPriority = 0;

    /** All the concrete shifts assigned to the doctor in the current schedule */
    @Transient
    List<ConcreteShift> assegnazioniTurnoCache;


    /**
     * Default constructor needed by Lombok
     */
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
                break;

            case LONG_SHIFT:
                this.longShiftPriority = this.partialLongShiftPriority;
                break;

            case NIGHT:
                this.nightPriority = this.partialNightPriority;
                break;

        }

    }

    public void updatePartialPriority(int priorityDelta, PriorityQueueEnum pq, int upperBound, int lowerBound) {  //addUffaTemp() counterpart
        int newPartialPriority;

        switch(pq) {
            case GENERAL:
                newPartialPriority = Math.max(this.generalPriority+priorityDelta, lowerBound);  //we ensure that new priority level stays into the bounds.
                newPartialPriority = Math.min(newPartialPriority, upperBound);
                this.partialGeneralPriority = newPartialPriority;
                break;

            case LONG_SHIFT:
                newPartialPriority = Math.max(this.longShiftPriority+priorityDelta, lowerBound);  //we ensure that new priority level stays into the bounds.
                newPartialPriority = Math.min(newPartialPriority, upperBound);
                this.partialLongShiftPriority = newPartialPriority;
                break;

            case NIGHT:
                newPartialPriority = Math.max(this.nightPriority+priorityDelta, lowerBound);  //we ensure that new priority level stays into the bounds.
                newPartialPriority = Math.min(newPartialPriority, upperBound);
                this.partialNightPriority = newPartialPriority;
                break;

        }

    }

}
