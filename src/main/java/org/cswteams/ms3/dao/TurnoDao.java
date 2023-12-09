package org.cswteams.ms3.dao;

import org.cswteams.ms3.entity.Shift;
import org.cswteams.ms3.enums.TipologiaTurno;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TurnoDao extends JpaRepository<Shift,Long> {

    List<Shift> findAllByServizioNome(String nomeServizio);
    List<Shift> findAllByServizioNomeAndTipologiaTurno(String nomeServizio, TipologiaTurno tipologiaTurno);

}
