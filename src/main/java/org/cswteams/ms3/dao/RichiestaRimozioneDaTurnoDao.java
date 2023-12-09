package org.cswteams.ms3.dao;

import org.cswteams.ms3.entity.RichiestaRimozioneDaTurno;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface RichiestaRimozioneDaTurnoDao extends JpaRepository<RichiestaRimozioneDaTurno, String> {

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
     * Controlla se esiste già, in base dati, una richiesta di rimozione da turno assegnato
     * per lo stesso utente e per lo stesso turno
     *
     * @param assegnazioneTurnoId id assegnazione turno
     * @param UtenteId            id utente
     * @return <code>true</code> se già esistente in base dati, <code>false</code> altrimenti.
     */
    @Query("SELECT  1 " +
            "FROM   RichiestaRimozioneDaTurno r " +
            "WHERE  r.assegnazioneTurno.id = ?1 " +
            "       AND " +
            "       r.utente.id = ?2")
    boolean checkIfAlreadyPresent(Long assegnazioneTurnoId, Long UtenteId);
}
