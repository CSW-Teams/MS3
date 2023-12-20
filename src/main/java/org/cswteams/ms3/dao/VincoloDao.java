package org.cswteams.ms3.dao;

import org.cswteams.ms3.entity.constraint.MS3Constraint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VincoloDao extends JpaRepository<MS3Constraint,Long> {

    @Query("select v from Constraint v where dtype = ?1")
    List<MS3Constraint> findByType(String typeValue);
}
