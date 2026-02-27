package org.cswteams.ms3.entity.constraint;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.cswteams.ms3.entity.ConcreteShift;
import org.cswteams.ms3.exception.ViolatedConstraintException;
import org.cswteams.ms3.exception.ViolatedVincoloAssegnazioneTurnoTurnoException;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * Implementa il vincolo {@code ConstraintMaxOrePeriodo}, che limita i minuti totali lavorati
 * da un medico in una finestra di giorni configurabile (durata della finestra + minuti massimi).
 *
 * Fa parte del "Catalogo vincoli attivi" (Microtask 1.2).
 *
 * @see docs/AI_powered_rescheduling/sprint_4/story_1.md#microtask-12--vincoli-e-pipeline-priorità-baseline
 */
@Entity
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class ConstraintMaxOrePeriodo extends ConstraintAssegnazioneTurnoTurno {
    /**
     * Durata del periodo, in giorni.
     */
    @NotNull
    @Column(name = "period_duration")
    private int periodDuration;

    /**
     * Tempo massimo di lavoro nel periodo, in minuti.
     */
    @NotNull
    @Column(name = "period_max_time")
    private long periodMaxTime;

    /**
     * Default constructor needed by Lombok
     */
    public ConstraintMaxOrePeriodo() {
    }

    public ConstraintMaxOrePeriodo(int periodDuration, long periodMaxTime){
        this.periodDuration = periodDuration;
        this.periodMaxTime = periodMaxTime;
    }

    /**
     * Verifica se il vincolo {@code ConstraintMaxOrePeriodo} è rispettato quando si tenta di assegnare
     * un nuovo {@link ConcreteShift turno concreto} a un medico. Il vincolo è violato se, aggiungendo
     * il nuovo turno, il tempo totale di lavoro del medico all'interno del {@code periodDuration}
     * supera {@code periodMaxTime}.
     *
     * @param context Oggetto {@link ContextConstraint} che comprende il nuovo turno concreto da assegnare
     *                e le informazioni sullo stato del medico nello schedule corrispondente.
     * @throws ViolatedConstraintException Eccezione lanciata se il vincolo è violato.
     */
    @Override
    public void verifyConstraint(ContextConstraint context) throws ViolatedConstraintException {
        if (context == null
                || context.getDoctorUffaPriority() == null
                || context.getDoctorUffaPriority().getSchedule() == null) {
            throw new IllegalStateException("Missing schedule in ContextConstraint for ConstraintMaxOrePeriodo");
        }
        if (context.getConcreteShift() == null) {
            throw new IllegalStateException("Missing concreteShift in ContextConstraint for ConstraintMaxOrePeriodo");
        }
        if (context.getConcreteShift().getShift() == null) {
            throw new IllegalStateException("Missing shift in current concreteShift for ConstraintMaxOrePeriodo");
        }
        if (context.getConcreteShift().getShift().getDuration() == null) {
            throw new IllegalStateException("Missing duration in current concreteShift shift for ConstraintMaxOrePeriodo");
        }

        List<ConcreteShift> concreteShiftList = context.getDoctorUffaPriority().getAssegnazioniTurnoCache();
        if(concreteShiftList != null && !concreteShiftList.isEmpty()) {
            //We find the bounds of the period to be considered in the schedule in which there is the new concrete shift to be assigned.
            long startPeriodDate = context.getDoctorUffaPriority().getSchedule().getStartDate();
            long endPeriodDate = startPeriodDate + periodDuration;
            while(endPeriodDate < context.getDoctorUffaPriority().getSchedule().getEndDate()){
                if(context.getConcreteShift().getDate() < endPeriodDate && (context.getConcreteShift().getDate() > startPeriodDate || context.getConcreteShift().getDate() == startPeriodDate)){
                    break;
                }
                startPeriodDate = endPeriodDate;
                endPeriodDate = endPeriodDate + periodDuration;
            }

            //We count the number of minutes composing the existent concrete shift assigned to our doctor in the considered period + the number of minutes composing the new concrete shift.
            long totalMinutes = context.getConcreteShift().getShift().getDuration().toMinutes();
            for(ConcreteShift concreteShift: concreteShiftList){
                if(concreteShift == null
                        || concreteShift.getShift() == null
                        || concreteShift.getShift().getDuration() == null) {
                    continue;
                }
                if(concreteShift.getDate() < endPeriodDate && (concreteShift.getDate() > startPeriodDate || concreteShift.getDate() == startPeriodDate)){
                    totalMinutes += concreteShift.getShift().getDuration().toMinutes();
                    if(totalMinutes > periodMaxTime){
                        throw new ViolatedVincoloAssegnazioneTurnoTurnoException(context.getConcreteShift(), context.getDoctorUffaPriority().getDoctor(), periodDuration, periodMaxTime);
                    }
                }
            }

        }

    }
}
