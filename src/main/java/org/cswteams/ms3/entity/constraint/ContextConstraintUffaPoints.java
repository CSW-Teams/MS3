package org.cswteams.ms3.entity.constraint;

import lombok.Data;
import org.cswteams.ms3.entity.ConcreteShift;
import org.cswteams.ms3.entity.scheduling.algo.DoctorScheduleState;

import javax.validation.constraints.NotNull;

public class ContextConstraintUffaPoints extends ContextConstraint{

    @NotNull
    private DoctorScheduleState doctorScheduleState;

    @NotNull
    private ConcreteShift concreteShift;

    public ContextConstraintUffaPoints(DoctorScheduleState doctorScheduleState, ConcreteShift concreteShift){
        this.concreteShift = concreteShift;
        this.doctorScheduleState = doctorScheduleState;
    }

}