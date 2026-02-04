package org.cswteams.ms3.entity.constraint;

import org.cswteams.ms3.entity.ConcreteShift;
import org.cswteams.ms3.exception.ViolatedConstraintException;
import org.cswteams.ms3.exception.ViolatedVincoloAssegnazioneTurnoTurnoException;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Implementa il vincolo di ubiquità ({@code ConstraintUbiquita}).
 * Questo vincolo impedisce sovrapposizioni temporali tra i turni assegnati allo stesso medico
 * (nessuna intersezione di finestre temporali).
 *
 * Fa parte del "Catalogo vincoli attivi" (Microtask 1.2).
 *
 * @see docs/AI_powered_rescheduling/sprint_4/story_1.md#microtask-12--vincoli-e-pipeline-priorità-baseline
 */
@Entity
public class ConstraintUbiquita extends ConstraintAssegnazioneTurnoTurno {

    /**
     * Verifica se il vincolo di ubiquità è rispettato quando si tenta di assegnare un nuovo
     * {@link ConcreteShift turno concreto} a un medico. Il vincolo è violato se il turno proposto
     * si sovrappone temporalmente con un turno già assegnato al medico.
     *
     * @param context Oggetto {@link ContextConstraint} che comprende il nuovo turno concreto da assegnare
     *                e le informazioni sullo stato del medico nello schedule corrispondente.
     * @throws ViolatedConstraintException Eccezione lanciata se il vincolo di ubiquità è violato,
     *                                     indicando una sovrapposizione di turni.
     */
    @Override
    public void verifyConstraint(ContextConstraint context) throws ViolatedConstraintException {

        if(!context.getDoctorUffaPriority().getAssegnazioniTurnoCache().isEmpty()){
            for(ConcreteShift assignedConcreteShift: context.getDoctorUffaPriority().getAssegnazioniTurnoCache()){
                LocalDate dateStartA = LocalDate.ofEpochDay(assignedConcreteShift.getDate());   //conversion Epoch -> LocalDate of assignedConcreteShift.getDate()
                LocalDateTime startA = dateStartA.atTime(assignedConcreteShift.getShift().getStartTime());

                LocalDate dateStartB = LocalDate.ofEpochDay(context.getConcreteShift().getDate());   //conversion Epoch -> LocalDate of context.getConcreteShift().getDate()
                LocalDateTime startB = dateStartB.atTime(context.getConcreteShift().getShift().getStartTime());

                LocalDateTime endA = startA.plus(assignedConcreteShift.getShift().getDuration());
                LocalDateTime endB = startB.plus(context.getConcreteShift().getShift().getDuration());

                if(!((startA.isBefore(startB) && (endA.isBefore(startB) || endA.isEqual(startB))) || (startB.isBefore(startA) && (endB.isBefore(startA) || endB.isEqual(startA))))){
                    throw new ViolatedVincoloAssegnazioneTurnoTurnoException(context.getConcreteShift(), assignedConcreteShift, context.getDoctorUffaPriority().getDoctor());
                }
            }
        }

    }

}
