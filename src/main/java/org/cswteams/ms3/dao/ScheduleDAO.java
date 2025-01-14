package org.cswteams.ms3.dao;

import org.cswteams.ms3.entity.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.persistence.Table;
import java.util.List;

@Repository
public interface ScheduleDAO extends JpaRepository<Schedule,Long> {

    @Query("select sc from Schedule sc where sc.startDate <= :date and sc.endDate >= :date")
    Schedule findByDateBetween(@Param("date") long date);

    @Query("select sc from Schedule sc where sc.violatedConstraints.size != 0")
    List<Schedule> leggiSchedulazioniIllegali();




}
