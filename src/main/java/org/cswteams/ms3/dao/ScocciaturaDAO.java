package org.cswteams.ms3.dao;

import org.cswteams.ms3.entity.scocciature.Scocciatura;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ScocciaturaDAO extends JpaRepository<Scocciatura,Long> {

}
