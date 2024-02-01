package org.cswteams.ms3.entity.constraint;

import lombok.Data;
import org.cswteams.ms3.entity.ConcreteShift;
import org.cswteams.ms3.entity.DoctorHolidays;
import org.cswteams.ms3.entity.scheduling.algo.DoctorUffaPriority;
import org.cswteams.ms3.entity.Holiday;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * Context for <i>constraints</i> validation.
 */
@Data
public class ContextConstraintPriority {

    @NotNull
    private DoctorUffaPriority doctorUffaPriority;

    @NotNull
    private ConcreteShift concreteShift;

    private DoctorHolidays doctorHolidays;

    private List<Holiday> holidays;

    public ContextConstraintPriority(DoctorUffaPriority doctorUffaPriority, ConcreteShift concreteShift, DoctorHolidays doctorHolidays, List<Holiday> holidays){
        this.concreteShift = concreteShift;
        this.doctorUffaPriority = doctorUffaPriority;
        this.doctorHolidays = doctorHolidays;
        this.holidays = holidays;
    }

}
