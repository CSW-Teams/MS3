package org.cswteams.ms3.dao;

import org.cswteams.ms3.entity.AssegnazioneTurno;
import org.cswteams.ms3.entity.CategoriaUtente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Set;

public interface CategoriaUtenteDao extends JpaRepository<CategoriaUtente,Long> {

    @Query(nativeQuery = true, value = "SELECT * FROM UTENTE_CATEGORIE AS US, CATEGORIA_UTENTE AS CU WHERE UTENTE_ID=:idPersona AND US.categorie_id=CU.id")
    Set<CategoriaUtente> findCategorieUtente(@Param("idPersona") Long id);

    @Query(nativeQuery = true,value="SELECT * FROM UTENTE_SPECIALIZZAZIONI AS US, CATEGORIA_UTENTE AS CU WHERE UTENTE_ID=:idPersona AND US.specializzazioni_id=CU.id")
    Set<CategoriaUtente> findSpecializzazioneUtente(@Param("idPersona") Long id);

}