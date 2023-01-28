package org.cswteams.ms3.dao;

import org.cswteams.ms3.entity.Liberatoria;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LiberatoriaDao extends JpaRepository<Liberatoria, Long> {

    Liberatoria findDeliberaByName(String name);
}
