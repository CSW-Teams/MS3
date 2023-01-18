package org.cswteams.ms3.control.vincoli;

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

    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    private List<CategoriaUtentiEnum> categorieVincolate;


    public VincoloMaxPeriodoConsecutivo() {
    }

    public VincoloMaxPeriodoConsecutivo(int maxConsecutiveMinutes, List<CategoriaUtentiEnum> categorieVincolate){
        this.maxConsecutiveMinutes = maxConsecutiveMinutes;
        this.categorieVincolate = categorieVincolate;
    }


    @Override
    public void verificaVincolo(ContestoVincolo contesto) throws ViolatedConstraintException {
        if(verificaAppartenenzaCategoria(contesto)) {
            List<AssegnazioneTurno> turniAssegnati = contesto.getUserScheduleState().getAssegnazioniTurnoCache();
            List<AssegnazioneTurno> turniConsecutivi = new ArrayList<>();

            //Verifico se l'ultimo turno assegnato è consecutivo con quello che sto per assegnare
            if (turniAssegnati.size() != 0 && verificaContiguitàAssegnazioneTurni(turniAssegnati.get(turniAssegnati.size() - 1), contesto.getAssegnazioneTurno())) {
                turniConsecutivi.add(turniAssegnati.get(turniAssegnati.size() - 1));
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
                for (AssegnazioneTurno turno : turniConsecutivi) {
                    minutiConsecutivi += turno.getTurno().getMinutidiLavoro();
                }
                if (minutiConsecutivi > maxConsecutiveMinutes) {
                    throw new ViolatedVincoloAssegnazioneTurnoTurnoException(contesto.getAssegnazioneTurno());
                }
            }
        }

    }

    private boolean verificaAppartenenzaCategoria(ContestoVincolo contesto){
         for(CategoriaUtentiEnum categoriaVincolo: this.categorieVincolate) {
             for (CategoriaUtente categoriaUtente : contesto.getUserScheduleState().getUtente().getCategorie()) {
                 if (categoriaUtente.getCategoria().compareTo(categoriaVincolo) == 0) {
                     if ((categoriaUtente.getInizioValidità().isBefore(contesto.getAssegnazioneTurno().getData()) || categoriaUtente.getInizioValidità().isEqual(contesto.getAssegnazioneTurno().getData())) && (categoriaUtente.getFineValidità().isAfter(contesto.getAssegnazioneTurno().getData()) || categoriaUtente.getFineValidità().isEqual(contesto.getAssegnazioneTurno().getData()))) {
                         return true;
                     }
                 }
             }
         }
         return false;
    }
}
