package org.cswteams.ms3.dao;

import org.cswteams.ms3.entity.QuantityShiftSeniority;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RuoloNumeroDAO extends JpaRepository<QuantityShiftSeniority,Long> {

}
