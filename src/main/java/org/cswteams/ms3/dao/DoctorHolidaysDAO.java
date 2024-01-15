package org.cswteams.ms3.dao;

import org.cswteams.ms3.entity.DoctorHolidays;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DoctorHolidaysDAO extends JpaRepository<DoctorHolidays,Long> {

    List<DoctorHolidays> findAll();
    DoctorHolidays findByDoctor_Id(Long id);

}
