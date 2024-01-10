package org.cswteams.ms3.entity.constraint;

import lombok.Data;
import org.cswteams.ms3.entity.ConcreteShift;
import org.cswteams.ms3.entity.DoctorScheduleState;

import javax.validation.constraints.NotNull;

@Data
public class ContestoVincolo {

    @NotNull
    private DoctorScheduleState doctorScheduleState;

    @NotNull
    private ConcreteShift concreteShift;

    public ContestoVincolo(DoctorScheduleState doctorScheduleState, ConcreteShift concreteShift){
        this.concreteShift = concreteShift;
        this.doctorScheduleState = doctorScheduleState;
    }

}
