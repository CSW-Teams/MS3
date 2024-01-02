package org.cswteams.ms3.dao;

import org.cswteams.ms3.entity.condition.PermanentCondition;
import org.cswteams.ms3.entity.condition.TemporaryCondition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PermanentConditionDAO extends JpaRepository<PermanentCondition, String> {

    List<PermanentCondition> findAll();

    PermanentCondition findByType(String type);

}
