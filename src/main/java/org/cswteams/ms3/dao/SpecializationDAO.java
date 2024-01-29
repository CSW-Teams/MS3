package org.cswteams.ms3.dao;

import org.cswteams.ms3.entity.Specialization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SpecializationDAO extends JpaRepository<Specialization, String> {
    List<Specialization> findAll();
    Specialization findByType(String specialization);
}
