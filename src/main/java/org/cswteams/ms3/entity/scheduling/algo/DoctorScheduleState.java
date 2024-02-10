package org.cswteams.ms3.entity.scheduling.algo;
//TODO generalizzare con DoctorUffaPriority

import java.util.ArrayList;
import java.util.List;

import javax.persistence.*;

import lombok.Getter;
import lombok.Setter;
import org.cswteams.ms3.entity.ConcreteShift;
import org.cswteams.ms3.entity.DoctorAssignment;
import org.cswteams.ms3.entity.Schedule;
import org.cswteams.ms3.entity.Doctor;

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
public class DoctorScheduleState extends DoctorXY {



    //funzione che ritorna la lista di ConcrateShift in un assegnazione corrente
    public List<ConcreteShift> getAssegnazioniTurnoCache() {

        if (assegnazioniTurnoCache == null) {
            this.assegnazioniTurnoCache = new ArrayList<>();
            for (ConcreteShift concreteShift : schedule.getConcreteShifts()) {
                for (DoctorAssignment da : concreteShift.getDoctorAssignmentList()) {
                    if (da.getDoctor().getId() == this.doctor.getId()) {
                        assegnazioniTurnoCache.add(concreteShift);
                        break;
                    }
                }
            }
        }
        return assegnazioniTurnoCache;

    }



    public DoctorScheduleState() {
    }

    public DoctorScheduleState(Doctor doctor, Schedule schedule) {

        this.doctor = doctor;
        this.schedule = schedule;
    }
}