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
     Doctor findByEmailAndPassword(String email, String password);
     Doctor findByEmail(String email);

     @Query("SELECT d FROM Doctor d WHERE d.seniority IN :seniorities")
     List<Doctor> findBySeniorities(@Param("seniorities") List<Seniority> seniorities);
}
