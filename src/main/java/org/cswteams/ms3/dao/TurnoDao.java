package org.cswteams.ms3.dao;

import org.cswteams.ms3.entity.Servizio;
import org.cswteams.ms3.entity.Turno;
import org.cswteams.ms3.enums.TipologiaTurno;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;

public interface TurnoDao extends JpaRepository<Turno,Long> {

    List<Turno> findAllByServizioNome(String nomeServizio);
    Turno findAllByServizioNomeAndTipologiaTurno(String nomeServizio, TipologiaTurno tipologiaTurno);

}
