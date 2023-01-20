package org.cswteams.ms3.entity.vincoli;

import org.cswteams.ms3.entity.AssegnazioneTurno;
import org.cswteams.ms3.entity.Categoria;
import org.cswteams.ms3.entity.CategoriaUtente;
import org.cswteams.ms3.exception.ViolatedConstraintException;
import org.cswteams.ms3.exception.ViolatedVincoloAssegnazioneTurnoTurnoException;

import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementa il vincolo del numero massimo di ore consecutive che un utente può fare
 */
@Entity
public class VincoloMaxPeriodoConsecutivo extends VincoloAssegnazioneTurnoTurno {

    private long maxConsecutiveMinutes;
    @ManyToMany
    private List<Categoria> categorieVincolate;


    public VincoloMaxPeriodoConsecutivo() {
    }

    public VincoloMaxPeriodoConsecutivo(int maxConsecutiveMinutes, List<Categoria> categorieVincolate){
        this.maxConsecutiveMinutes = maxConsecutiveMinutes;
        this.categorieVincolate = categorieVincolate;
    }


    @Override
    public void verificaVincolo(ContestoVincolo contesto) throws ViolatedConstraintException {
        if(contesto.getUserScheduleState().getAssegnazioniTurnoCache().size() != 0 && verificaAppartenenzaCategoria(contesto)) {
            List<AssegnazioneTurno> turniAssegnati = contesto.getUserScheduleState().getAssegnazioniTurnoCache();
            List<AssegnazioneTurno> turniConsecutivi = new ArrayList<>();

            //Prendo l'indice del turno precedente all'assegnazione che sto per fare
            int prevAssegnazioneIdx = getAssegnazioneTurnoPrecedenteIdx(turniAssegnati,contesto.getAssegnazioneTurno());

            //Verifico se il turno assegnato precedente è consecutivo con quello che sto per assegnare
            if (prevAssegnazioneIdx > -1 && verificaContiguitàAssegnazioneTurni(turniAssegnati.get(prevAssegnazioneIdx), contesto.getAssegnazioneTurno())) {
                turniConsecutivi.add(turniAssegnati.get(prevAssegnazioneIdx));
                turniConsecutivi.add(contesto.getAssegnazioneTurno());

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

            if (succAssegnazioneIdx < turniAssegnati.size() && verificaContiguitàAssegnazioneTurni(contesto.getAssegnazioneTurno(),turniAssegnati.get(succAssegnazioneIdx))) {
                if(turniConsecutivi.size() == 0) {
                    turniConsecutivi.add(contesto.getAssegnazioneTurno());
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
            for (AssegnazioneTurno turno : turniConsecutivi) {
                minutiConsecutivi += turno.getTurno().getMinutidiLavoro();
            }
            if (minutiConsecutivi > maxConsecutiveMinutes) {
                throw new ViolatedVincoloAssegnazioneTurnoTurnoException(contesto.getAssegnazioneTurno(), contesto.getUserScheduleState().getUtente(), maxConsecutiveMinutes);
            }
        }


    }

    private boolean verificaAppartenenzaCategoria(ContestoVincolo contesto){
        if(categorieVincolate.size() == 0){
            return true;
        }
         for(Categoria categoriaVincolo: this.categorieVincolate) {
             for (CategoriaUtente categoriaUtente : contesto.getUserScheduleState().getUtente().getStato()) {
                 if (categoriaUtente.getCategoria().getNome().compareTo(categoriaVincolo.getNome()) == 0) {
                     if ((categoriaUtente.getInizioValidità().isBefore(contesto.getAssegnazioneTurno().getData()) || categoriaUtente.getInizioValidità().isEqual(contesto.getAssegnazioneTurno().getData())) && (categoriaUtente.getFineValidità().isAfter(contesto.getAssegnazioneTurno().getData()) || categoriaUtente.getFineValidità().isEqual(contesto.getAssegnazioneTurno().getData()))) {
                         return true;
                     }
                 }
             }
         }
         return false;
    }
}
