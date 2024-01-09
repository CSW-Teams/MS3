package org.cswteams.ms3.entity;

import lombok.Getter;
import org.cswteams.ms3.enums.TaskEnum;

import javax.persistence.*;

@Entity
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "task_id", nullable = false)
    @Getter
    private Long id;

    @Enumerated
    @Getter
    private TaskEnum taskType;

    protected Task(){

    }

    /**
     * Class that has the responsibility to map tasks to medical services.
     * @param taskType Type of all the possible tasks to be assigned to
     */
    public Task(TaskEnum taskType){
        this.taskType = taskType;
    }
}
