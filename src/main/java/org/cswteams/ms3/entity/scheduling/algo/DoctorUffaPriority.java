package org.cswteams.ms3.entity.scheduling.algo;

import org.cswteams.ms3.entity.ConcreteShift;
import org.cswteams.ms3.entity.Doctor;
import org.cswteams.ms3.entity.DoctorAssignment;
import org.cswteams.ms3.entity.Schedule;
import org.cswteams.ms3.enums.PriorityQueueEnum;

import java.lang.Math;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.*;

import lombok.Getter;
import lombok.Setter;

/**
 * All the information about priority levels on the queues of the doctors.
 */
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
public class DoctorUffaPriority extends DoctorXY{

    private int partialGeneralPriority = 0;
    private int generalPriority = 0;
    private int partialLongShiftPriority = 0;
    private int longShiftPriority = 0;
    private int partialNightPriority = 0;
    private int nightPriority = 0;


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
    @Override
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