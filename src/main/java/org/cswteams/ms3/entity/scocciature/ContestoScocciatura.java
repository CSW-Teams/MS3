package org.cswteams.ms3.entity.scocciature;

import lombok.Data;
import org.cswteams.ms3.entity.ConcreteShift;
import org.cswteams.ms3.entity.DoctorScheduleState;
import org.cswteams.ms3.entity.UserScheduleState;

@Data
public class ContestoScocciatura {

    private DoctorScheduleState doctorScheduleState;

    private ConcreteShift concreteShift;

    public ContestoScocciatura(DoctorScheduleState utente, ConcreteShift turno){
        this.concreteShift = turno;
        this.doctorScheduleState = utente;
    }

}
