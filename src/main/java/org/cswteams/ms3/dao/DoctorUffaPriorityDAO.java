package org.cswteams.ms3.dao;

import org.cswteams.ms3.entity.scheduling.algo.DoctorUffaPriority;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DoctorUffaPriorityDAO extends JpaRepository<DoctorUffaPriority,Long> {

    List<DoctorUffaPriority> findAll();
    List<DoctorUffaPriority> findByDoctor_Id(Long id);

}
