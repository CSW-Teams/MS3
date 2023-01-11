package org.cswteams.ms3.dao;

import org.cswteams.ms3.entity.AssegnazioneTurno;
import org.cswteams.ms3.entity.CategoriaUtente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Set;

public interface CategoriaUtenteDao extends JpaRepository<CategoriaUtente,Long> {

    @Query(nativeQuery = true, value = "SELECT * FROM CATEGORIA_UTENTE, UTENTE_CATEGORIE WHERE utente_id=:idPersona")
    Set<CategoriaUtente> findCategorieUtente(@Param("idPersona") Long id);
}