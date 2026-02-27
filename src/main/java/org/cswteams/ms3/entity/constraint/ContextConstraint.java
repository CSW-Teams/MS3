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
 * Rappresenta il contesto per la valutazione dei vincoli.
 *
 * Durante la generazione degli schedule, {@link ScheduleBuilder} costruisce un {@code ContextConstraint}
 * che combina il medico candidato ({@link DoctorUffaPriority}), il {@link ConcreteShift} target,
 * la mappa ferie/festività del medico ({@link DoctorHolidays}) e l'elenco delle festività di sistema ({@link Holiday}).
 *
 * Questo oggetto è il contesto unico passato a tutti i vincoli per decidere se l'assegnazione è ammissibile.
 *
 * Per maggiori dettagli sulla pipeline dei vincoli, si veda:
 * @see docs/AI_powered_rescheduling/sprint_4/story_1.md#microtask-12--vincoli-e-pipeline-priorità-baseline
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
