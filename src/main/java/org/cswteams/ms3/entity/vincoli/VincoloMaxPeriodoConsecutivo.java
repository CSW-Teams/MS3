package org.cswteams.ms3.entity.vincoli;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.cswteams.ms3.entity.ConcreteShift;
import org.cswteams.ms3.entity.category.Condition;
import org.cswteams.ms3.entity.category.TemporaryCondition;
import org.cswteams.ms3.exception.ViolatedConstraintException;
import org.cswteams.ms3.exception.ViolatedVincoloAssegnazioneTurnoTurnoException;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementa il vincolo del numero massimo di ore consecutive che un utente può fare
 */
@Entity
@Data
@EqualsAndHashCode(callSuper = true)
public class VincoloMaxPeriodoConsecutivo extends VincoloAssegnazioneTurnoTurno {

    private long maxConsecutiveMinutes;
    @ManyToOne
    private TemporaryCondition categoriaVincolata;


    public VincoloMaxPeriodoConsecutivo() {
    }

    public VincoloMaxPeriodoConsecutivo(int maxConsecutiveMinutes, TemporaryCondition categoriaVincolate){
        this.maxConsecutiveMinutes = maxConsecutiveMinutes;
        this.categoriaVincolata = categoriaVincolate;
    }

    public VincoloMaxPeriodoConsecutivo(int maxConsecutiveMinutes){
        this.maxConsecutiveMinutes = maxConsecutiveMinutes;
        this.categoriaVincolata = null;
    }


    @Override
    public void verificaVincolo(ContestoVincolo contesto) throws ViolatedConstraintException {
        if(contesto.getUserScheduleState().getAssegnazioniTurnoCache().size() != 0 && verificaAppartenenzaCategoria(contesto)) {
            List<ConcreteShift> turniAssegnati = contesto.getUserScheduleState().getAssegnazioniTurnoCache();
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
                minutiConsecutivi += turno.getShift().getMinutidiLavoro();
            }
            if (minutiConsecutivi > maxConsecutiveMinutes) {
                throw new ViolatedVincoloAssegnazioneTurnoTurnoException(contesto.getConcreteShift(), contesto.getUserScheduleState().getDoctor(), maxConsecutiveMinutes);
            }
        }


    }

    private boolean verificaAppartenenzaCategoria(ContestoVincolo contesto){
        if(categoriaVincolata == null){
            return true;
        }
         for (Condition categoriaUtente : contesto.getUserScheduleState().getDoctor().getPermanentConditions()) {
             if (categoriaUtente.getType().compareTo(categoriaVincolata.getType()) == 0) {
                 //if ((categoriaUtente.getFineValidità().isBefore(contesto.getConcreteShift().getData()) || categoriaUtente.getInizioValidità().isEqual(contesto.getConcreteShift().getData())) && (categoriaUtente.getFineValidità().isAfter(contesto.getConcreteShift().getData()) || categoriaUtente.getFineValidità().isEqual(contesto.getConcreteShift().getData()))) {
                     return true;
                 //}
             }
         }

     return false;
    }
}
