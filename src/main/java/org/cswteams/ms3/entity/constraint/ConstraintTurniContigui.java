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
 * This class implements the idea for which shift tipologies (i.e. time slots) may impose some constraints on the
 * presence of other shifts which are contiguous to it.
 * For instance, a nocturne shift brings to a period in which the involved doctor should not be allocated in any other shift.
 */
@Getter
@Setter
@NoArgsConstructor
@Entity
public class ConstraintTurniContigui extends ConstraintAssegnazioneTurnoTurno {

    /**
     * Round of temporal units in which it is prohibited to assign the same doctor to another shift whose category
     * is part of the forbidden ones
     */
    @NotNull
    private int horizon;

    /** Horizon temporal unit */
    @Enumerated(EnumType.STRING)
    @NotNull
    @Column(name = "t_unit")
    private ChronoUnit tUnit;
    
    /** Time slot which causes the constraint */
    @Enumerated(EnumType.STRING)
    @NotNull
    @Column(name = "time_slot")
    private TimeSlot timeSlot;

    /** Time slots forbidden by the constraint */
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
     * This method checks if TipologieTurniContigue constraint is respected while inserting a new concrete shift into a schedule.
     * @param context Object comprehending the new concrete shift to be assigned and the information about doctor's state in the corresponding schedule
     * @throws ViolatedConstraintException Exception thrown if the constraint is violated
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
