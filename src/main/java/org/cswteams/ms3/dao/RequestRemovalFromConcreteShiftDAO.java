package org.cswteams.ms3.dao;

import org.cswteams.ms3.entity.RequestRemovalFromConcreteShift;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface RequestRemovalFromConcreteShiftDAO extends JpaRepository<RequestRemovalFromConcreteShift, Long> {

    /**
     * Ritorna una lista di tutte le richieste di rimozione da turno assegnato <i>pending</i>,
     * ossia non esaminate, i.e. con campo <code>esaminata</code> settato a <code>false</code>.
     *
     * @return lista di richieste di rimozione da turno assegnato <i>pending</i>
     */
    @Query("SELECT  r " +
            "FROM   RequestRemovalFromConcreteShift r " +
            "WHERE  r.isReviewed = false")
    List<RequestRemovalFromConcreteShift> findAllPending();

    /**
     * Ritorna una lista di tutte le richieste di rimozione da turno assegnato
     * per una specifica <code>AssegnazioneTurno</code> ed uno specifico <code>Utente</code>.
     * La cardinalità di tale lista dovrebbe essere sempre 0 o 1 (se la richiesta esiste, è unica).
     *
     * @param assegnazioneTurnoId id assegnazione turno
     * @param UtenteId            id utente
     * @return lista, eventualmente vuota, di richieste di rimozione da turno assegnato per la specifica
     * <code>AssegnazioneTurno</code> e per lo specifico <code>Utente</code>
     */
    @Query("SELECT  r " +
            "FROM   RequestRemovalFromConcreteShift r " +
            "WHERE  r.concreteShift.id = ?1 " +
            "       AND " +
            "       r.requestingDoctor.id = ?2")
    List<RequestRemovalFromConcreteShift> findAllByAssegnazioneTurnoIdAndUtenteId(Long assegnazioneTurnoId, Long UtenteId);

    /**
     * Ritorna una lista di tutte le richieste di rimozione da turno assegnato
     * per uno specifico <code>Utente</code>.
     *
     * @param UtenteId id utente
     * @return lista, eventualmente vuota, di richieste di rimozionde da turno assegnato
     * per lo specifico <code>Utente</code>.
     */
    @Query("SELECT  r " +
            "FROM   RequestRemovalFromConcreteShift r " +
            "WHERE  r.requestingDoctor.id = ?1")
    List<RequestRemovalFromConcreteShift> findAllByUser(Long UtenteId);
}
