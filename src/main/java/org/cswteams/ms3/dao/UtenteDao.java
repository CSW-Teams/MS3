package org.cswteams.ms3.dao;

import org.cswteams.ms3.entity.Utente;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UtenteDao extends JpaRepository<Utente,Long> {

     Utente findById(long id);
     Utente findByEmailAndPassword(String email, String password);

     Utente findByEmail(String email);

}
