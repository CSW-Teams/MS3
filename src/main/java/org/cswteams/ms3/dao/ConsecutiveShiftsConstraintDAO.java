package org.cswteams.ms3.dao;

import org.cswteams.ms3.entity.constraint.ConstraintTurniContigui;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConsecutiveShiftsConstraintDAO extends JpaRepository<ConstraintTurniContigui, Long>{
    
}
