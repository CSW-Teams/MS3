package org.cswteams.ms3.entity.vincoli;

import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import org.cswteams.ms3.entity.AssegnazioneTurno;
import org.cswteams.ms3.enums.TipologiaTurno;
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
public class VincoloTipologieTurniContigue extends VincoloAssegnazioneTurnoTurno {

    /** Intorno di unità temporali in cui è proibito assegnare lo stesso utente a un altro turno
     * la cui categoria rientri in quelle vietate
     */
    private int horizon;

    /** unità temporale di horizon */
    @Enumerated(EnumType.STRING)
    private ChronoUnit tUnit;
    
    /** Tipologia turno che impone il vincolo */
    @Enumerated(EnumType.STRING)
    private TipologiaTurno tipologiaTurno;

    /** Tipologie Turno vietate da questo vincolo */
    @ElementCollection
    @Enumerated(EnumType.STRING)
    private Set<TipologiaTurno> tipologieTurnoVietate;

    public VincoloTipologieTurniContigue(int horizon, ChronoUnit hours, TipologiaTurno notturno, HashSet<TipologiaTurno> hashSet) {
        this.horizon = horizon;
        this.tUnit = hours;
        this.tipologiaTurno = notturno;
        this.tipologieTurnoVietate = hashSet;
    }

    @Override
    public void verificaVincolo(ContestoVincolo contesto) throws ViolatedConstraintException {
                
        // we check if the shift to be allocated is of the type that must be excluded the constraint
        if (tipologieTurnoVietate.contains(contesto.getAssegnazioneTurno().getTurno().getTipologiaTurno())){
            
            // we search for another allocated shift of the same user in the horizon
            List<AssegnazioneTurno> ats = contesto.getUserScheduleState().getAssegnazioniTurnoCache();
            for (AssegnazioneTurno at : ats) {
                if (at.getTurno().getTipologiaTurno() == tipologiaTurno
                        && verificaContiguitàAssegnazioneTurni(at, contesto.getAssegnazioneTurno(), tUnit, horizon)) {
                    throw new ViolatedVincoloAssegnazioneTurnoTurnoException(at, contesto.getAssegnazioneTurno(), contesto.getUserScheduleState().getUtente());
                }
            }
        }
        
    }

}
