package org.cswteams.ms3.dao;

import org.cswteams.ms3.entity.Config;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConfigDAO extends JpaRepository<Config, String>{
    
}
