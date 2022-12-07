package org.cswteams.ms3.dao;

import org.cswteams.ms3.entity.Turno;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.sql.Timestamp;
import java.util.List;


public interface TurnoDao extends JpaRepository<Turno,Long> {

    List<Turno> findByInizioAndFine(Timestamp inizio, Timestamp fine);

    @Query(nativeQuery = true, value = "SELECT * FROM TURNO_UTENTI AS TU, TURNO AS T WHERE TU.TURNO_ID=T.ID AND TU.utenti_id=:idPersona")
    List<Turno> findTurniUtente(@Param("idPersona") Long id);

}
