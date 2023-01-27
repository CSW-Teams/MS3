package org.cswteams.ms3.dao;

import org.cswteams.ms3.entity.RuoloNumero;
import org.cswteams.ms3.entity.Turno;
import org.cswteams.ms3.enums.TipologiaTurno;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RuoloNumeroDao extends JpaRepository<RuoloNumero,Long> {

}
