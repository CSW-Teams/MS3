package org.cswteams.ms3.dao;

import org.cswteams.ms3.entity.scheduling.algo.DoctorScheduleState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DoctorScheduleStateDAO extends JpaRepository<DoctorScheduleState, Long> {

    List<DoctorScheduleState> findAll();
    List<DoctorScheduleState> findByDoctor_Id(Long id);
}
