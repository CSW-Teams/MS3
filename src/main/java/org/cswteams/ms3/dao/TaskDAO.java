package org.cswteams.ms3.dao;

import org.cswteams.ms3.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface TaskDAO extends JpaRepository<Task, Long> {

    /**
     * Check if the specific <code>Task</code> is assigned,
     * i.e. if at least one entry of <code>DoctorAssignment</code> exist.
     * <p>
     * See issue #413 for further details.
     *
     * @param taskId task id
     * @see <a href="https://github.com/CSW-Teams/MS3/issues/413">Issue #413</a>
     * @return <code>true</code> if found, <code>false</code> elsewhere.
     */
    @Query(value =
            "SELECT " +
                    "   CASE WHEN EXISTS " +
                    "       (" +
                    "       SELECT  * " +
                    "       FROM    doctor_assignment da " +
                    "       WHERE   da.task_task_id = ?1 " +
                    "       LIMIT 1" +
                    "       )" +
                    "    THEN 'TRUE'  " +
                    "    ELSE 'FALSE' " +
                    "END",
            nativeQuery = true)
    boolean isTaskAssigned(Long taskId);
}
