package org.cswteams.ms3.entity.constraint;

import lombok.Data;
import org.cswteams.ms3.entity.ConcreteShift;
import org.cswteams.ms3.entity.DoctorHolidays;
import org.cswteams.ms3.entity.DoctorUffaPriority;
import org.cswteams.ms3.entity.Holiday;

import javax.persistence.Column;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * Context for <i>constraints</i> validation.
 */
@Data
@Table(name = "context_constraint")
public class ContextConstraint {

    @NotNull
    @Column(name = "doctor_uffa_priority")
    private DoctorUffaPriority doctorUffaPriority;

    @NotNull
    @Column(name = "concrete_shift")
    private ConcreteShift concreteShift;

    @Column(name = "doctor_holidays")
    private DoctorHolidays doctorHolidays;

    private List<Holiday> holidays;

    public ContextConstraint(DoctorUffaPriority doctorUffaPriority, ConcreteShift concreteShift, DoctorHolidays doctorHolidays, List<Holiday> holidays){
        this.concreteShift = concreteShift;
        this.doctorUffaPriority = doctorUffaPriority;
        this.doctorHolidays = doctorHolidays;
        this.holidays = holidays;
    }

}
