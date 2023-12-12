package org.cswteams.ms3.dao;

import org.cswteams.ms3.entity.category.Structure;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RotationDao extends JpaRepository<Structure, String> {
    List<Structure> findAll();
}
