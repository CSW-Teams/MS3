package org.cswteams.ms3.entity.constraint;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.cswteams.ms3.entity.ConcreteShift;
import org.cswteams.ms3.entity.condition.Condition;
import org.cswteams.ms3.exception.ViolatedConstraintException;
import org.cswteams.ms3.exception.ViolatedVincoloAssegnazioneTurnoTurnoException;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

/**
 * This class implements the maximum number of consecutive minutes that a doctor can work.
 *
 * @see ConfigVincoli for the configuration of this constraint.
 */
@Entity
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class ConstraintMaxPeriodoConsecutivo extends ConstraintAssegnazioneTurnoTurno {
    @NotNull
    private long maxConsecutiveMinutes;
    @ManyToOne
    private Condition constrainedCategory;


    public ConstraintMaxPeriodoConsecutivo() {
    }

    public ConstraintMaxPeriodoConsecutivo(int maxConsecutiveMinutes, Condition constrainedCategory){
        this.maxConsecutiveMinutes = maxConsecutiveMinutes;
        this.constrainedCategory = constrainedCategory;
    }

    public ConstraintMaxPeriodoConsecutivo(int maxConsecutiveMinutes){
        this.maxConsecutiveMinutes = maxConsecutiveMinutes;
        this.constrainedCategory = null;
    }

    /**
     * This method checks if maxPeriodoConsecutivo constraint is respected while inserting a new concrete shift into a schedule.
     *
     * @param context Object comprehending the new concrete shift to be assigned and the information about doctor's state in the corresponding schedule
     * @throws ViolatedConstraintException Exception thrown if the constraint is violated
     * @see ConstraintMaxPeriodoConsecutivo
     */
    @Override
    public void verifyConstraint(ContextConstraint context) throws ViolatedConstraintException {
    /*
        if(context.getConcreteShift().getShift().getDuration().toMinutes() > maxConsecutiveMinutes) {
            throw new ViolatedVincoloAssegnazioneTurnoTurnoException(context.getConcreteShift(), context.getDoctorUffaPriority().getDoctor(), maxConsecutiveMinutes);
        }
    */
        if(context.getDoctorUffaPriority().getAssegnazioniTurnoCache().size() != 0 && verificaAppartenenzaCategoria(context)) {
            List<ConcreteShift> assignedConcreteShifts = context.getDoctorUffaPriority().getAssegnazioniTurnoCache();
            List<ConcreteShift> consecConcreteShifts = new ArrayList<>();

            //Prendo l'indice del turno precedente all'assegnazione che sto per fare
            int prevAssegnazioneIdx = getAssegnazioneTurnoPrecedenteIdx(assignedConcreteShifts,context.getConcreteShift());

            //Verifico se il turno assegnato precedente è consecutivo con quello che sto per assegnare
            if (prevAssegnazioneIdx > -1 && verificaContiguitàAssegnazioneTurni(assignedConcreteShifts.get(prevAssegnazioneIdx), context.getConcreteShift())) {
                consecConcreteShifts.add(assignedConcreteShifts.get(prevAssegnazioneIdx));
                consecConcreteShifts.add(context.getConcreteShift());

                // Controllo se nei turni già assegnati c'è una catena di turni contigui
                for (int i = prevAssegnazioneIdx; i > 0; i--) {
                    // Controlla che siano consecutivi
                    if (verificaContiguitàAssegnazioneTurni(assignedConcreteShifts.get(i - 1), assignedConcreteShifts.get(i))) {
                        consecConcreteShifts.add(assignedConcreteShifts.get(i - 1));
                    } else break;

                }
            }
            //Verifico se il turno successivo a quello che sto per assegnare è contiguo
            int succAssegnazioneIdx = prevAssegnazioneIdx + 1;

            if (succAssegnazioneIdx < assignedConcreteShifts.size() && verificaContiguitàAssegnazioneTurni(context.getConcreteShift(),assignedConcreteShifts.get(succAssegnazioneIdx))) {
                if(consecConcreteShifts.isEmpty()) {
                    consecConcreteShifts.add(context.getConcreteShift());
                }
                consecConcreteShifts.add(assignedConcreteShifts.get(succAssegnazioneIdx));

                // Controllo se nei turni già assegnati c'è una catena di turni contigui
                for (int i = succAssegnazioneIdx; i < assignedConcreteShifts.size()-1; i++) {
                    // Controlla che siano consecutivi
                    if (verificaContiguitàAssegnazioneTurni(assignedConcreteShifts.get(i), assignedConcreteShifts.get(i+1))) {
                        consecConcreteShifts.add(assignedConcreteShifts.get(i+1));
                    } else break;

                }
            }

            long consecMinutes = 0;
            // Controllo che la somma delle ore non sia superata con la nuova assegnazione
            for (ConcreteShift turno : consecConcreteShifts) {
                consecMinutes += turno.getShift().getDuration().toMinutes();
            }
            if (consecMinutes > maxConsecutiveMinutes) {
                throw new ViolatedVincoloAssegnazioneTurnoTurnoException(context.getConcreteShift(), context.getDoctorUffaPriority().getDoctor(), maxConsecutiveMinutes);
            }
        }


    }

    /**
     * Private method which verifies if the doctor belongs to a specific category (condition): remember that max number of consecutive minutes depends on the doctor condition.
     * @param context Object comprehending the new concrete shift to be assigned and the information about doctor's state in the corresponding schedule
     * @return Boolean that represents if the doctor belongs (right now) to the category
     */
    private boolean verificaAppartenenzaCategoria(ContextConstraint context){

        if(constrainedCategory == null){
            return true;
        }
         for (Condition condition : context.getDoctorUffaPriority().getDoctor().getPermanentConditions()) {
             if (condition.getType().compareTo(constrainedCategory.getType()) == 0) {
                 //if ((condition.getStartDate() < context.getConcreteShift().getDate() || condition.getStartDate() == context.getConcreteShift().getDate()) && (condition.getEndDate() > context.getConcreteShift().getDate()) || condition.getEndDate() == context.getConcreteShift().getDate()) {
                     return true;
                 //}
             }
         }

     return false;
    }
}
