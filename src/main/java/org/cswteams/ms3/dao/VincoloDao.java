package org.cswteams.ms3.dao;

import org.cswteams.ms3.entity.vincoli.Vincolo;
import org.cswteams.ms3.entity.vincoli.VincoloMaxPeriodoConsecutivo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface VincoloDao extends JpaRepository<Vincolo,Long> {

    @Query("select v from Vincolo v where dtype = ?1")
    List<Vincolo> findByType(String typeValue);
}
