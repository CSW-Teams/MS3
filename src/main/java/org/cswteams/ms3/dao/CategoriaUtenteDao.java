package org.cswteams.ms3.dao;

import org.cswteams.ms3.entity.CategoriaUtente;
import org.cswteams.ms3.entity.CategoriaUtenteId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoriaUtenteDao extends JpaRepository<CategoriaUtente, CategoriaUtenteId> {

    List<CategoriaUtente> findAllByUtenteId(Long utenteId);

}
