package org.cswteams.ms3.dao;

import org.cswteams.ms3.entity.ConcreteShift;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface ConcreteShiftDAO extends JpaRepository<ConcreteShift,Long> {

    @Query(nativeQuery = true, value = "SELECT * FROM ASSEGNAZIONE_TURNO_UTENTI_DI_GUARDIA AS GUARDIA, ASSEGNAZIONE_TURNO_UTENTI_REPERIBILI AS REPERIBILI, ASSEGNAZIONE_TURNO AS T WHERE (GUARDIA.ASSEGNAZIONE_TURNO_ID=T.ID AND  GUARDIA.UTENTI_DI_GUARDIA_ID=:idPersona) OR (REPERIBILI.ASSEGNAZIONE_TURNO_ID=T.ID AND REPERIBILI.UTENTI_REPERIBILI_ID=:idPersona)")
    Set<ConcreteShift> findTurniUtente(@Param("idPersona") Long id);

}


