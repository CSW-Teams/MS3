package org.cswteams.ms3.entity.constraint;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.cswteams.ms3.entity.ConcreteShift;
import org.cswteams.ms3.entity.condition.Condition;
import org.cswteams.ms3.entity.condition.TemporaryCondition;
import org.cswteams.ms3.exception.ViolatedConstraintException;
import org.cswteams.ms3.exception.ViolatedVincoloAssegnazioneTurnoTurnoException;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementa il vincolo del numero massimo di ore consecutive che un utente può fare
 */
@Entity
@Data
@EqualsAndHashCode(callSuper = true)
public class ConstraintMaxPeriodoConsecutivo extends ConstraintAssegnazioneTurnoTurno {
    @NotNull
    private long maxConsecutiveMinutes;
    @ManyToOne
    @NotNull
    private TemporaryCondition categoriaVincolata;


    public ConstraintMaxPeriodoConsecutivo() {
    }

    public ConstraintMaxPeriodoConsecutivo(int maxConsecutiveMinutes, TemporaryCondition categoriaVincolate){
        this.maxConsecutiveMinutes = maxConsecutiveMinutes;
        this.categoriaVincolata = categoriaVincolate;
    }

    public ConstraintMaxPeriodoConsecutivo(int maxConsecutiveMinutes){
        this.maxConsecutiveMinutes = maxConsecutiveMinutes;
        this.categoriaVincolata = null;
    }


    @Override
    public void verificaVincolo(ContestoVincolo contesto) throws ViolatedConstraintException {
        if(contesto.getDoctorScheduleState().getAssegnazioniTurnoCache().size() != 0 && verificaAppartenenzaCategoria(contesto)) {
            List<ConcreteShift> turniAssegnati = contesto.getDoctorScheduleState().getAssegnazioniTurnoCache();
            List<ConcreteShift> turniConsecutivi = new ArrayList<>();

            //Prendo l'indice del turno precedente all'assegnazione che sto per fare
            int prevAssegnazioneIdx = getAssegnazioneTurnoPrecedenteIdx(turniAssegnati,contesto.getConcreteShift());

            //Verifico se il turno assegnato precedente è consecutivo con quello che sto per assegnare
            if (prevAssegnazioneIdx > -1 && verificaContiguitàAssegnazioneTurni(turniAssegnati.get(prevAssegnazioneIdx), contesto.getConcreteShift())) {
                turniConsecutivi.add(turniAssegnati.get(prevAssegnazioneIdx));
                turniConsecutivi.add(contesto.getConcreteShift());

                // Controllo se nei turni già assegnati c'è una catena di turni contigui
                for (int i = prevAssegnazioneIdx; i > 0; i--) {
                    // Controlla che siano consecutivi
                    if (verificaContiguitàAssegnazioneTurni(turniAssegnati.get(i - 1), turniAssegnati.get(i))) {
                        turniConsecutivi.add(turniAssegnati.get(i - 1));
                    } else break;

                }
            }
            //Verifico se il turno successivo a quello che sto per assegnare è contiguo
            int succAssegnazioneIdx = prevAssegnazioneIdx +1;

            if (succAssegnazioneIdx < turniAssegnati.size() && verificaContiguitàAssegnazioneTurni(contesto.getConcreteShift(),turniAssegnati.get(succAssegnazioneIdx))) {
                if(turniConsecutivi.size() == 0) {
                    turniConsecutivi.add(contesto.getConcreteShift());
                }
                turniConsecutivi.add(turniAssegnati.get(succAssegnazioneIdx));

                // Controllo se nei turni già assegnati c'è una catena di turni contigui
                for (int i = succAssegnazioneIdx; i < turniAssegnati.size()-1; i++) {
                    // Controlla che siano consecutivi
                    if (verificaContiguitàAssegnazioneTurni(turniAssegnati.get(i), turniAssegnati.get(i+1))) {
                        turniConsecutivi.add(turniAssegnati.get(i+1));
                    } else break;

                }
            }

            long minutiConsecutivi = 0;
            // Controllo che la somma delle ore non sia superata con la nuova assegnazione
            for (ConcreteShift turno : turniConsecutivi) {
                minutiConsecutivi += turno.getShift().getDuration().toMinutes();
            }
            if (minutiConsecutivi > maxConsecutiveMinutes) {
                throw new ViolatedVincoloAssegnazioneTurnoTurnoException(contesto.getConcreteShift(), contesto.getDoctorScheduleState().getDoctor(), maxConsecutiveMinutes);
            }
        }


    }

    private boolean verificaAppartenenzaCategoria(ContestoVincolo contesto){

        if(categoriaVincolata == null){
            return true;
        }
         for (Condition categoriaUtente : contesto.getDoctorScheduleState().getDoctor().getPermanentConditions()) {
             if (categoriaUtente.getType().compareTo(categoriaVincolata.getType()) == 0) {
                 //if ((categoriaUtente.getFineValidità().isBefore(contesto.getConcreteShift().getData()) || categoriaUtente.getInizioValidità().isEqual(contesto.getConcreteShift().getData())) && (categoriaUtente.getFineValidità().isAfter(contesto.getConcreteShift().getData()) || categoriaUtente.getFineValidità().isEqual(contesto.getConcreteShift().getData()))) {
                     return true;
                 //}
             }
         }

     return false;
    }
}
