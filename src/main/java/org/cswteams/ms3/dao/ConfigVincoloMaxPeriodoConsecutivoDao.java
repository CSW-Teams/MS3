package org.cswteams.ms3.dao;

import org.cswteams.ms3.entity.vincoli.ConfigVincoloMaxPeriodoConsecutivo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ConfigVincoloMaxPeriodoConsecutivoDao extends JpaRepository<ConfigVincoloMaxPeriodoConsecutivo, Long> {

    List<ConfigVincoloMaxPeriodoConsecutivo> findAllByCategoriaVincolataNome(String nome);
}
