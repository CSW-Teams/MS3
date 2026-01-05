package org.cswteams.ms3.dao;

import org.cswteams.ms3.entity.ScheduleFeedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ScheduleFeedbackDAO extends JpaRepository<ScheduleFeedback, Long> {
    
    @Query("SELECT DISTINCT f FROM ScheduleFeedback f JOIN FETCH f.concreteShifts LEFT JOIN FETCH f.doctor")
    List<ScheduleFeedback> findAll();

    @Query("SELECT DISTINCT f FROM ScheduleFeedback f JOIN FETCH f.concreteShifts LEFT JOIN FETCH f.doctor WHERE f.doctor.id = :doctorId")
    List<ScheduleFeedback> findByDoctorId(Long doctorId);
}
