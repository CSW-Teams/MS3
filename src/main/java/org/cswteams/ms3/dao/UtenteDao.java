package org.cswteams.ms3.dao;

import org.cswteams.ms3.entity.doctor.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UtenteDao extends JpaRepository<Doctor,Long> {

     Doctor findById(long id);
     Doctor findByEmailAndPassword(String email, String password);

     Doctor findByEmail(String email);

}
