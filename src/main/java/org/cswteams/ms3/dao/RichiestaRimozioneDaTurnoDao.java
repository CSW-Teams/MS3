package org.cswteams.ms3.dao;

import org.cswteams.ms3.entity.RichiestaRimozioneDaTurno;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface RichiestaRimozioneDaTurnoDao extends JpaRepository<RichiestaRimozioneDaTurno, Long> {

    /**
     * Ritorna una lista di tutte le richieste di rimozione da turno assegnato <i>pending</i>,
     * ossia non esaminate, i.e. con campo <code>esaminata</code> settato a <code>false</code>.
     *
     * @return lista di richieste di rimozione da turno assegnato <i>pending</i>
     */
    @Query("SELECT  r " +
            "FROM   RichiestaRimozioneDaTurno r " +
            "WHERE  r.esaminata = false")
    List<RichiestaRimozioneDaTurno> findAllPending();

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
            "FROM   RichiestaRimozioneDaTurno r " +
            "WHERE  r.assegnazioneTurno.id = ?1 " +
            "       AND " +
            "       r.utente.id = ?2")
    List<RichiestaRimozioneDaTurno> findAllByAssegnazioneTurnoIdAndUtenteId(Long assegnazioneTurnoId, Long UtenteId);

    @Query("SELECT  r " +
            "FROM   RichiestaRimozioneDaTurno r " +
            "WHERE  r.utente.id = ?1")
    List<RichiestaRimozioneDaTurno> findAllByUser(Long UtenteId);
}
