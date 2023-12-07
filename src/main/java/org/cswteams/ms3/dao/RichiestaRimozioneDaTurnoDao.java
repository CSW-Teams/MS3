package org.cswteams.ms3.dao;

import org.cswteams.ms3.entity.Config;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RichiestaRimozioneDaTurnoDao extends JpaRepository<Config, String> {
}
