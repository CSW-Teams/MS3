package org.cswteams.ms3.dao;

import org.cswteams.ms3.entity.Servizio;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ServizioDao extends JpaRepository<Servizio,String> {
    Servizio findByNome(String nome);
}
