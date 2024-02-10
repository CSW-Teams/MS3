package org.cswteams.ms3.entity.scocciature;

import lombok.Data;
import org.cswteams.ms3.entity.ConcreteShift;
import org.cswteams.ms3.entity.scheduling.algo.DoctorXY;
import org.cswteams.ms3.entity.scheduling.factory.SchedulerType;

@Data
public class ContestoScocciatura {

    private DoctorXY doctorUffaPriority;

    private SchedulerType schedulerType; // per distinzione pesi da assegnare

    private ConcreteShift concreteShift;

    public ContestoScocciatura(DoctorXY utente, ConcreteShift turno, SchedulerType schedulerType){
        this.concreteShift = turno;
        this.doctorUffaPriority = utente;
        this.schedulerType=schedulerType;
    }

}
