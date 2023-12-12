package org.cswteams.ms3.dao;

import org.cswteams.ms3.entity.vincoli.VincoloTipologieTurniContigue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VincoloTipologieTurniContigueDao extends JpaRepository<VincoloTipologieTurniContigue, Long>{
    
}
