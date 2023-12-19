package org.cswteams.ms3.dao;

import org.cswteams.ms3.entity.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ScheduleDao extends JpaRepository<Schedule,Long> {

    @Query("select sc from Schedule sc where sc.startDate <= ?1 and sc.endDate >= ?1")
    Schedule findByDateBetween(long date);

    @Query("select sc from Schedule sc where sc.violatedConstraints.size != 0")
    List<Schedule> leggiSchedulazioniIllegali();




}
