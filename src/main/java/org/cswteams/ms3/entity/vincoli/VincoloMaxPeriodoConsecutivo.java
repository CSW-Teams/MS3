package org.cswteams.ms3.entity.vincoli;

import org.cswteams.ms3.entity.AssegnazioneTurno;
import org.cswteams.ms3.exception.ViolatedConstraintException;
import org.cswteams.ms3.exception.ViolatedVincoloAssegnazioneTurnoTurnoException;

import javax.persistence.Entity;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementa il vincolo del numero massimo di ore consecutive che un utente può fare
 */
@Entity
public class VincoloMaxPeriodoConsecutivo extends VincoloAssegnazioneTurnoTurno{

    private long maxConsecutiveMinutes;

    public VincoloMaxPeriodoConsecutivo() {
    }

    public VincoloMaxPeriodoConsecutivo(int maxConsecutiveMinutes){
        this.maxConsecutiveMinutes = maxConsecutiveMinutes;
    }


    @Override
    public void verificaVincolo(ContestoVincolo contesto) throws ViolatedConstraintException {
        List<AssegnazioneTurno> turniAssegnati = contesto.getUserScheduleState().getAssegnazioniTurnoCache();
        List<AssegnazioneTurno> turniConsecutivi = new ArrayList<>();

        //Verifico se l'ultimo turno assegnato è consecutivo con quello che sto per assegnare
        if(turniAssegnati.size() != 0 && verificaContiguitàAssegnazioneTurni(turniAssegnati.get(turniAssegnati.size()-1),contesto.getAssegnazioneTurno())){
            turniConsecutivi.add(turniAssegnati.get(turniAssegnati.size()-1));
            turniConsecutivi.add(contesto.getAssegnazioneTurno());

            // Controllo se nei turni già assegnati c'è una catena di turni contigui
            for (int i = turniAssegnati.size() - 1; i > 0; i--) {
                // Controlla che siano consecutivi
                if (verificaContiguitàAssegnazioneTurni(turniAssegnati.get(i - 1), turniAssegnati.get(i))) {
                    turniConsecutivi.add(turniAssegnati.get(i - 1));
                } else break;

            }
            long minutiConsecutivi = 0;
            // Controllo che la somma delle ore non sia superata con la nuova assegnazione
            for(AssegnazioneTurno turno: turniConsecutivi){
                minutiConsecutivi += turno.getTurno().getMinutidiLavoro();           }
            if (minutiConsecutivi > maxConsecutiveMinutes) {
                throw new ViolatedVincoloAssegnazioneTurnoTurnoException(contesto.getAssegnazioneTurno(), contesto.getUserScheduleState().getUtente(), maxConsecutiveMinutes);
            }
        }

    }
}
