package org.cswteams.ms3.dto.medicalservice;

import lombok.Getter;
import lombok.Setter;
import org.cswteams.ms3.enums.TaskEnum;

import javax.persistence.*;

/**
 * This DTO keeps track of whether the task is already assigned to some <code>DoctorAssignment</code>.
 * <p>
 * This feature is useful for making impossible to update/delete a <code>Task</code> that
 * is assigned to some <code>DoctorAssignment</code>.
 * <p>
 * See issue #413 for more details.
 */
@Entity
public class TaskWithAssignmentDTO {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "task_id", nullable = false)
    @Getter
    private Long id;

    @Enumerated
    @Getter
    private TaskEnum taskType;

    /**
     * This flag is set to <code>true</code> if some <code>DoctorAssignment</code> is linked
     * to this task.
     * <p>
     * This feature is useful for making impossible to update/delete a <code>Task</code> that
     * is assigned to some <code>DoctorAssignment</code>.
     * <p>
     * See issue #413 for more details.
     */
    @Getter
    private boolean isAssigned = false;

    protected TaskWithAssignmentDTO() {

    }

    public TaskWithAssignmentDTO(Long id, TaskEnum taskType, boolean isAssigned) {
        this.id = id;
        this.taskType = taskType;
        this.isAssigned = isAssigned;
    }
}
