package org.cswteams.ms3.dao;

import org.cswteams.ms3.entity.category.PermanentCondition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PermanentConditionDao extends JpaRepository<PermanentCondition, String> {

    List<PermanentCondition> findAll();

}
