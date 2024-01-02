package org.cswteams.ms3.dao;

import org.cswteams.ms3.entity.Specialization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SpecializationDAO extends JpaRepository<Specialization, String> {
    List<Specialization> findAll();
    List<Specialization> findAllById(Long id);
    /*@Query("SELECT s.type FROM Specialization as s JOIN Doctor as d WHERE d.id = :id")
    @Query("select Specialization.type from Specialization, Doctor where Doctor.id = :id")
    List<String> findByIdType(@Param("id") Long id);*/
    //List<Specialization> getAllByType(@Param("id")  Long id) ;
}
