package org.cswteams.ms3.dao;

import org.cswteams.ms3.entity.MedicalService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ServiceDAO extends JpaRepository<MedicalService,String> {
    MedicalService findByLabel(String nome);
}
