package org.cswteams.ms3.dao;

import org.cswteams.ms3.entity.CategoriaUtente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Set;

public interface CategoriaUtenteDao extends JpaRepository<CategoriaUtente,Long> {

    @Query(nativeQuery = true, value = "SELECT * from categoria_utente as cu, utente_stato as us where us.stato_id=cu.id and us.utente_id=:idPersona")
    Set<CategoriaUtente> findStatoUtente(@Param("idPersona") Long id);

    @Query(nativeQuery = true,value="SELECT * from categoria_utente as cu, utente_specializzazioni as us where us.specializzazioni_id=cu.id and us.utente_id=:idPersona")
    Set<CategoriaUtente> findSpecializzazioniUtente(@Param("idPersona") Long id);

    @Query(nativeQuery = true,value="SELECT * from categoria_utente as cu, utente_turnazioni as us where us.turnazioni_id=cu.id and us.utente_id=:idPersona")
    Set<CategoriaUtente> findTurnazioniUtente(@Param("idPersona") Long id);



}