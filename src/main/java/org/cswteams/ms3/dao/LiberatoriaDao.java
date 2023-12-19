package org.cswteams.ms3.dao;

import org.cswteams.ms3.entity.Waiver;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LiberatoriaDao extends JpaRepository<Waiver, Long> {

    Waiver findDeliberaByName(String name);
}
