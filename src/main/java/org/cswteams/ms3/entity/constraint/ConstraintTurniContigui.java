package org.cswteams.ms3.entity.constraint;

import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

import org.cswteams.ms3.entity.ConcreteShift;
import org.cswteams.ms3.enums.TimeSlot;
import org.cswteams.ms3.exception.ViolatedConstraintException;
import org.cswteams.ms3.exception.ViolatedVincoloAssegnazioneTurnoTurnoException;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Implementa il vincolo {@code ConstraintTurniContigui}, che vieta turni troppo ravvicinati
 * in base a un "time slot trigger" e a un orizzonte temporale specificato (es. blocco dopo un turno notturno).
 *
 * Fa parte del "Catalogo vincoli attivi" (Microtask 1.2).
 *
 * @see docs/AI_powered_rescheduling/sprint_4/story_1.md#microtask-12--vincoli-e-pipeline-priorità-baseline
 */
@Getter
@Setter
@NoArgsConstructor
@Entity
public class ConstraintTurniContigui extends ConstraintAssegnazioneTurnoTurno {

    /**
     * Orizzonte temporale, in {@link ChronoUnit unità di tempo}, entro il quale è proibito assegnare
     * lo stesso medico a un altro turno la cui categoria rientra in quelle proibite.
     */
    @NotNull
    private int horizon;

    /** Unità temporale dell'orizzonte (es. ORE, GIORNI). */
    @Enumerated(EnumType.STRING)
    @NotNull
    @Column(name = "t_unit")
    private ChronoUnit tUnit;
    
    /** {@link TimeSlot Time slot} che scatena il vincolo (es. NOTTURNO). */
    @Enumerated(EnumType.STRING)
    @NotNull
    @Column(name = "time_slot")
    private TimeSlot timeSlot;

    /** {@link TimeSlot Time slots} proibiti dal vincolo. */
    @ElementCollection
    @Enumerated(EnumType.STRING)
    @CollectionTable(
            name = "constraint_turni_contigui_forbidden_time_slots",
            joinColumns = @JoinColumn(name = "constraint_turni_contigui_constraint_id")
    )
    @NotNull
    @Column(name = "forbidden_time_slots")
    private Set<TimeSlot> forbiddenTimeSlots;

    public ConstraintTurniContigui(int horizon, ChronoUnit hours, TimeSlot notturno, HashSet<TimeSlot> hashSet) {
        this.horizon = horizon;
        this.tUnit = hours;
        this.timeSlot = notturno;
        this.forbiddenTimeSlots = hashSet;
    }

    /**
     * Verifica se il vincolo {@code ConstraintTurniContigui} è rispettato quando si tenta di assegnare
     * un nuovo {@link ConcreteShift turno concreto} a un medico. Il vincolo è violato se il turno
     * proposto si trova all'interno dell'orizzonte temporale proibito da un turno "trigger"
     * (specificato da {@code timeSlot}) o se il turno proposto è esso stesso un turno "trigger"
     * e si trova troppo vicino a un altro turno proibito.
     *
     * @param context Oggetto {@link ContextConstraint} che comprende il nuovo turno concreto da assegnare
     *                e le informazioni sullo stato del medico nello schedule corrispondente.
     * @throws ViolatedConstraintException Eccezione lanciata se il vincolo è violato.
     */
    @Override
    public void verifyConstraint(ContextConstraint context) throws ViolatedConstraintException {

        // We check if the shift to be allocated is of the type that must be excluded the constraint
        if (forbiddenTimeSlots.contains(context.getConcreteShift().getShift().getTimeSlot())){
            // Get already assigned shift of the user in the current schedule which is getting generated
            List<ConcreteShift> concreteShiftList = context.getDoctorUffaPriority().getAssegnazioniTurnoCache();

            // Check if there is a shift with the timeSlot constrained too near to the actual considering shift
            for (ConcreteShift concreteShift : concreteShiftList) {
                if (concreteShift.getShift().getTimeSlot() == timeSlot
                        && verificaContiguitàAssegnazioneTurni(concreteShift, context.getConcreteShift(), tUnit, horizon)) {
                    throw new ViolatedVincoloAssegnazioneTurnoTurnoException(concreteShift, context.getConcreteShift(), context.getDoctorUffaPriority().getDoctor());
                }
            }

            // Check if the considering shift has a timeslot such that it must respect the constraints
            if(context.getConcreteShift().getShift().getTimeSlot() == timeSlot){
                for (ConcreteShift concreteShift : concreteShiftList) {
                    if (verificaContiguitàAssegnazioneTurni(concreteShift, context.getConcreteShift(), tUnit, horizon)) {
                        throw new ViolatedVincoloAssegnazioneTurnoTurnoException(concreteShift,
                                context.getConcreteShift(), context.getDoctorUffaPriority().getDoctor());
                    }
                }
            }
        }
    }

}
