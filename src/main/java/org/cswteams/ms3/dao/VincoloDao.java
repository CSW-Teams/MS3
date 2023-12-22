package org.cswteams.ms3.dao;

import org.cswteams.ms3.entity.constraint.Constraint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VincoloDao extends JpaRepository<Constraint,Long> {

    @Query("select v from Constraint v where dtype = ?1")
    List<Constraint> findByType(String typeValue);
}
