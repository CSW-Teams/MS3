package org.cswteams.ms3.dao;

import org.cswteams.ms3.entity.RuoloNumero;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RuoloNumeroDao extends JpaRepository<RuoloNumero,Long> {

}
