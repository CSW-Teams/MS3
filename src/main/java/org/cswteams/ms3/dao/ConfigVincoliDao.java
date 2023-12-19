package org.cswteams.ms3.dao;

import org.cswteams.ms3.entity.constraint.ConfigVincoli;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConfigVincoliDao extends JpaRepository<ConfigVincoli, Long> {
}
