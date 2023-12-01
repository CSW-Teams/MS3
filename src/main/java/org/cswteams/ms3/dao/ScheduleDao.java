package org.cswteams.ms3.dao;

import org.cswteams.ms3.dto.ScheduloDTO;
import org.cswteams.ms3.entity.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

public interface ScheduleDao extends JpaRepository<Schedule,Long> {

    @Query("select sc from Schedule sc where sc.startDateEpochDay <= ?1 and sc.endDateEpochDay >= ?1")
    Schedule findByDateBetween(LocalDate date);

    @Query("select sc from Schedule sc where sc.isIllegal = true")
    List<Schedule> leggiSchedulazioniIllegali();




}
