package org.cswteams.ms3.dao;

import org.cswteams.ms3.entity.Servizio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ServizioDao extends JpaRepository<Servizio,String> {
    Servizio findByNome(String nome);
}
