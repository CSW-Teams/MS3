package org.cswteams.ms3.dao;

import org.cswteams.ms3.entity.constraint.ConfigVincMaxPerCons;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ConfigVincoloMaxPeriodoConsecutivoDAO extends JpaRepository<ConfigVincMaxPerCons, Long> {

    List<ConfigVincMaxPerCons> findAllByConstrainedConditionType(String type);
}
