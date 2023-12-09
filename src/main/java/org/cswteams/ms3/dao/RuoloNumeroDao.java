package org.cswteams.ms3.dao;

import org.cswteams.ms3.entity.RuoloNumero;
import org.cswteams.ms3.entity.Turno;
import org.cswteams.ms3.enums.TipologiaTurno;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RuoloNumeroDao extends JpaRepository<RuoloNumero,Long> {

}
