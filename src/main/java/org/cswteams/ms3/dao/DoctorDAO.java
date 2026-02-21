package org.cswteams.ms3.dao;

import org.cswteams.ms3.entity.Doctor;
import org.cswteams.ms3.enums.Seniority;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DoctorDAO extends JpaRepository<Doctor,Long> {

     Doctor findById(long id);

     @Query("SELECT d FROM Doctor d " +
             "WHERE d.email = :email " +
             "AND d.password = :password " +
             "AND d.id = (SELECT MIN(d2.id) FROM Doctor d2 WHERE d2.email = :email AND d2.password = :password)")
     Doctor findByEmailAndPassword(@Param("email") String email, @Param("password") String password);

     @Query("SELECT d FROM Doctor d " +
             "WHERE d.email = :email " +
             "AND d.id = (SELECT MIN(d2.id) FROM Doctor d2 WHERE d2.email = :email)")
     Doctor findByEmail(@Param("email") String email);

     @Query("SELECT d FROM Doctor d WHERE d.seniority IN :seniorities")
     List<Doctor> findBySeniorities(@Param("seniorities") List<Seniority> seniorities);
}
