package org.cswteams.ms3.entity.constraint;

import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.NotNull;

import org.cswteams.ms3.entity.ConcreteShift;
import org.cswteams.ms3.enums.TimeSlot;
import org.cswteams.ms3.exception.ViolatedConstraintException;
import org.cswteams.ms3.exception.ViolatedVincoloAssegnazioneTurnoTurnoException;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Le tipologie di turno possono imporre dei vincoli sulla presenza di altri turni
 * contigui ad esso.
 * Ad esempio, una notte prevede un periodo di smonto notte entro il quale
 * l'utente partecipante non può essere allocato a nessun altro turno.
 */
@Getter
@Setter
@NoArgsConstructor
@Entity
public class ConstraintTipologieTurniContigue extends ConstraintAssegnazioneTurnoTurno {

    /** Intorno di unità temporali in cui è proibito assegnare lo stesso utente a un altro turno
     * la cui categoria rientri in quelle vietate
     */
    @NotNull
    private int horizon;

    /** unità temporale di horizon */
    @Enumerated(EnumType.STRING)
    @NotNull
    private ChronoUnit tUnit;
    
    /** Tipologia turno che impone il vincolo */
    @Enumerated(EnumType.STRING)
    @NotNull
    private TimeSlot timeSlot;

    /** Tipologie Shift vietate da questo vincolo */
    @ElementCollection
    @Enumerated(EnumType.STRING)
    @NotNull
    private Set<TimeSlot> tipologieTurnoVietate;

    public ConstraintTipologieTurniContigue(int horizon, ChronoUnit hours, TimeSlot notturno, HashSet<TimeSlot> hashSet) {
        this.horizon = horizon;
        this.tUnit = hours;
        this.timeSlot = notturno;
        this.tipologieTurnoVietate = hashSet;
    }

    @Override
    public void verificaVincolo(ContestoVincolo contesto) throws ViolatedConstraintException {
                
        // we check if the shift to be allocated is of the type that must be excluded the constraint
        if (tipologieTurnoVietate.contains(contesto.getConcreteShift().getShift().getTimeSlot())){
            
            // we search for another allocated shift of the same user in the horizon
            List<ConcreteShift> ats = contesto.getDoctorScheduleState().getAssegnazioniTurnoCache();
            for (ConcreteShift at : ats) {
                if (at.getShift().getTimeSlot() == timeSlot
                        && verificaContiguitàAssegnazioneTurni(at, contesto.getConcreteShift(), tUnit, horizon)) {
                    throw new ViolatedVincoloAssegnazioneTurnoTurnoException(at, contesto.getConcreteShift(), contesto.getDoctorScheduleState().getDoctor());
                }
            }
        }
        
    }

}
