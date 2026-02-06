package org.cswteams.ms3.entity.constraint;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.cswteams.ms3.entity.ConcreteShift;
import org.cswteams.ms3.entity.condition.Condition;
import org.cswteams.ms3.exception.ViolatedConstraintException;
import org.cswteams.ms3.exception.ViolatedVincoloAssegnazioneTurnoTurnoException;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementa il vincolo {@code ConstraintMaxPeriodoConsecutivo}, che limita il numero di minuti
 * consecutivi che un medico può lavorare. Può essere parametrizzato in base alla categoria/condizione
 * del medico (es. over-62, gravidanza).
 @see ConfigVincoli for the configuration of this constraint.
 * Fa parte del "Catalogo vincoli attivi" (Microtask 1.2).
 *
 * @see docs/AI_powered_rescheduling/sprint_4/story_1.md#microtask-12--vincoli-e-pipeline-priorità-baseline
 */
@Entity
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class ConstraintMaxPeriodoConsecutivo extends ConstraintAssegnazioneTurnoTurno {
    /**
     * Numero massimo di minuti consecutivi che un medico può lavorare.
     */
    @NotNull
    @Column(name = "max_consecutive_minutes")
    private long maxConsecutiveMinutes;

    /**
     * La {@link Condition condizione} (o categoria) del medico a cui si applica questo vincolo.
     * Se {@code null}, il vincolo si applica a tutti i medici.
     */
    @ManyToOne
    @JoinColumn(name = "constrained_category_id")
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
     * Verifica se il vincolo {@code ConstraintMaxPeriodoConsecutivo} è rispettato quando si tenta di assegnare
     * un nuovo {@link ConcreteShift turno concreto} a un medico. Il vincolo è violato se l'aggiunta del
     * nuovo turno crea una sequenza di turni contigui che supera {@code maxConsecutiveMinutes},
     * tenendo conto dell'eventuale {@link Condition categoria} del medico.
     *
     * @param context Oggetto {@link ContextConstraint} che comprende il nuovo turno concreto da assegnare
     *                e le informazioni sullo stato del medico nello schedule corrispondente.
     * @throws ViolatedConstraintException Eccezione lanciata se il vincolo è violato.
     */
    @Override
    public void verifyConstraint(ContextConstraint context) throws ViolatedConstraintException {
        if(context.getConcreteShift().getShift().getDuration().toMinutes() > maxConsecutiveMinutes) {
            throw new ViolatedVincoloAssegnazioneTurnoTurnoException(context.getConcreteShift(), context.getDoctorUffaPriority().getDoctor(), maxConsecutiveMinutes);
        }
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
     * Metodo privato che verifica se il medico appartiene a una specifica categoria ({@link Condition condizione}),
     * poiché il numero massimo di minuti consecutivi può dipendere dalla condizione del medico.
     *
     * @param context Oggetto {@link ContextConstraint} che comprende il {@link ConcreteShift turno concreto}
     *                da assegnare e le informazioni sullo stato del medico nello schedule corrispondente.
     * @return {@code true} se il medico appartiene (al momento) alla categoria specificata dal vincolo,
     *         o se il vincolo non è limitato a una categoria specifica; {@code false} altrimenti.
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
