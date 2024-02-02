package org.cswteams.ms3.entity.constraint;

import lombok.Data;
import org.cswteams.ms3.entity.ConcreteShift;
import org.cswteams.ms3.entity.scheduling.algo.DoctorScheduleState;
import org.cswteams.ms3.entity.scheduling.algo.DoctorXY;

import javax.validation.constraints.NotNull;

public class ContextConstraintUffaPoints extends ContextConstraint{


    public ContextConstraintUffaPoints(DoctorXY doctorScheduleState, ConcreteShift concreteShift){
        this.concreteShift = concreteShift;
        this.doctorXY = doctorScheduleState;
    }

}