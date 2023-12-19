package org.cswteams.ms3.dao;

import org.cswteams.ms3.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskDAO extends JpaRepository<Task, String> {

}
