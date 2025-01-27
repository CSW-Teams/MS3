package org.cswteams.ms3.dao;

import org.cswteams.ms3.dao.soft_delete.SoftDeleteJpaRepository;
import org.cswteams.ms3.entity.MedicalService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MedicalServiceDAO extends JpaRepository<MedicalService,Long>, SoftDeleteJpaRepository<MedicalService, Long> {
    MedicalService findByLabel(String nome);
}
