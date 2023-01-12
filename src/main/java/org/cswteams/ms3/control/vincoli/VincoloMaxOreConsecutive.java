package org.cswteams.ms3.control.vincoli;

import org.cswteams.ms3.entity.AssegnazioneTurno;
import org.cswteams.ms3.entity.Turno;
import org.cswteams.ms3.exception.ViolatedConstraintException;
import org.cswteams.ms3.exception.ViolatedVincoloAssegnazioneTurnoTurnoException;

import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementa il vincolo del numero massimo di ore consecutive che un utente può fare
 */
public class VincoloMaxOreConsecutive extends VincoloAssegnazioneTurnoTurno{

    private long maxConsecutiveMinutes;

    public VincoloMaxOreConsecutive(int maxConsecutiveMinutes){
        this.maxConsecutiveMinutes = maxConsecutiveMinutes;
    }


    @Override
    public void verificaVincolo(ContestoVincolo contesto) throws ViolatedConstraintException {
        List<AssegnazioneTurno> turniAssegnati = contesto.getUserScheduleState().getAssegnazioniTurnoCache();
        List<Turno> turniConsecutivi = new ArrayList<>();

        //Verifico se l'ultimo turno assegnato è consecutivo con quello che sto per assegnare
        if(verificaContiguitàAssegnazioneTurni(turniAssegnati.get(turniAssegnati.size()-1),contesto.getAssegnazioneTurno())){
            turniConsecutivi.add(turniAssegnati.get(turniAssegnati.size()-1).getTurno());
            turniConsecutivi.add(contesto.getAssegnazioneTurno().getTurno());

            // Controllo se nei turni già assegnati c'è una catena di turni contigui
            for (int i = turniAssegnati.size() - 1; i > 0; i--) {
                // Controlla che siano consecutivi
                if (verificaContiguitàAssegnazioneTurni(turniAssegnati.get(i - 1), turniAssegnati.get(i))) {
                    turniConsecutivi.add(turniAssegnati.get(i - 1).getTurno());
                } else break;

            }
            long minutiConsecutivi = 0;
            // Controllo che la somma delle ore non sia superata con la nuova assegnazione
            for(Turno turno: turniConsecutivi){
                minutiConsecutivi += turno.getOraInizio().until(turno.getOraFine(), ChronoUnit.MINUTES);
            }
            if (minutiConsecutivi > maxConsecutiveMinutes) {
                throw new ViolatedVincoloAssegnazioneTurnoTurnoException(contesto.getAssegnazioneTurno());
            }
        }

    }
}
