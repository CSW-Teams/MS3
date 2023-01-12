package org.cswteams.ms3.control.vincoli;

import org.cswteams.ms3.entity.AssegnazioneTurno;
import org.cswteams.ms3.entity.Turno;
import org.cswteams.ms3.exception.ViolatedConstraintException;
import org.cswteams.ms3.exception.ViolatedVincoloAssegnazioneTurnoTurnoException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementa il vincolo del numero massimo di ore consecutive che un utente può fare
 */
public class VincoloMaxPeriodoConsecutivo extends VincoloAssegnazioneTurnoTurno{

    private long maxConsecutiveMinutes;

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
                if(turno.getTurno().isGiornoSuccessivo()){
                    LocalDateTime inizio = LocalDateTime.of(LocalDate.ofEpochDay(turno.getDataEpochDay()),turno.getTurno().getOraInizio());
                    LocalDateTime fine = LocalDateTime.of(LocalDate.ofEpochDay(turno.getDataEpochDay()).plusDays(1),turno.getTurno().getOraFine());
                    minutiConsecutivi += inizio.until(fine, ChronoUnit.MINUTES);
                }
                else minutiConsecutivi += turno.getTurno().getOraInizio().until(turno.getTurno().getOraFine(), ChronoUnit.MINUTES);
            }
            if (minutiConsecutivi > maxConsecutiveMinutes) {
                throw new ViolatedVincoloAssegnazioneTurnoTurnoException(contesto.getAssegnazioneTurno());
            }
        }

    }
}
