package org.cswteams.ms3.dao;

import org.cswteams.ms3.entity.Turno;
import org.cswteams.ms3.enums.TipologiaTurno;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TurnoDao extends JpaRepository<Turno,Long> {

    List<Turno> findAllByServizioNome(String nomeServizio);
    List<Turno> findAllByServizioNomeAndTipologiaTurno(String nomeServizio, TipologiaTurno tipologiaTurno);

}
