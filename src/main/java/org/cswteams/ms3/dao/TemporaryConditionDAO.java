package org.cswteams.ms3.dao;

import org.cswteams.ms3.entity.condition.TemporaryCondition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TemporaryConditionDAO extends JpaRepository<TemporaryCondition, String> {
    List<TemporaryCondition> findAll();

}
