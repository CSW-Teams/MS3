package org.cswteams.ms3.dao;

import org.cswteams.ms3.entity.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScheduleDao extends JpaRepository<Schedule, Long> {

}
