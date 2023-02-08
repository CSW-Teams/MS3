package org.cswteams.ms3.dao;

import org.cswteams.ms3.entity.Desiderata;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DesiderataDao extends JpaRepository<Desiderata, Long> {

    List<Desiderata> findAllByUtenteId(Long utenteId);

}
