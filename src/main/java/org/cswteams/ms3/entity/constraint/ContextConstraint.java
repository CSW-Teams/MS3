package org.cswteams.ms3.entity.constraint;

import lombok.Data;
import org.cswteams.ms3.entity.ConcreteShift;
import org.cswteams.ms3.entity.DoctorHolidays;
import org.cswteams.ms3.entity.Holiday;
import org.cswteams.ms3.entity.scheduling.algo.DoctorScheduleState;
import org.cswteams.ms3.entity.scheduling.algo.DoctorUffaPriority;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * !!! - Classe temporanea per far funzionare i controlli sui vincoli di entrambi gli algoritmi di scheduling
 */
@Data
public abstract class ContextConstraint {

    @NotNull
    protected ConcreteShift concreteShift;

    protected DoctorHolidays doctorHolidays;

    protected List<Holiday> holidays;




    // ... in particolare, i seguenti due attributi appartengono ciascuno ad una delle due versioni dell'algoritmo

    @NotNull
    protected DoctorUffaPriority doctorUffaPriority;

    @NotNull
    protected DoctorScheduleState doctorScheduleState;


}
