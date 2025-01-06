package org.cswteams.ms3.DBperTenant.dao;

import org.cswteams.ms3.DBperTenant.entity.Hospital;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
@Repository
public interface HospitalDAO extends JpaRepository<Hospital, Long> {
}