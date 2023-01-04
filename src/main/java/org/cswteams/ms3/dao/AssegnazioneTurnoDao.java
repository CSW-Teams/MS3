package org.cswteams.ms3.dao;

import org.cswteams.ms3.entity.AssegnazioneTurno;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Set;


public interface AssegnazioneTurnoDao extends JpaRepository<AssegnazioneTurno,Long> {

    @Query(nativeQuery = true, value = "SELECT * FROM ASSEGNAZIONE_TURNO_UTENTI_DI_GUARDIA AS GUARDIA, ASSEGNAZIONE_TURNO_UTENTI_REPERIBILI AS REPERIBILI, ASSEGNAZIONE_TURNO AS T WHERE (GUARDIA.ASSEGNAZIONE_TURNO_ID=T.ID OR REPERIBILI.ASSEGNAZIONE_TURNO_ID=T.ID) AND REPERIBILI.UTENTI_REPERIBILI_ID=:idPersona AND GUARDIA.UTENTI_DI_GUARDIA_ID=:idPersona")
    Set<AssegnazioneTurno> findTurniUtente(@Param("idPersona") Long id);

}


