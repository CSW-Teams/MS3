package org.cswteams.ms3.entity;

import lombok.Getter;
import org.cswteams.ms3.enums.TaskEnum;

import javax.persistence.*;

/**
 * Class that has the responsibility to map tasks to <i>medical services</i>,
 * e.g. ward, clinic, emergency room, and so on.
 *
 * @see <a href="https://github.com/CSW-Teams/MS3/wiki#mansioni">Glossary</a>.
 * @see TaskEnum
 */
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
     * Create a new <i>task</i>, of the specified type.
     *
     * @param taskType Type of all the possible tasks to be assigned to
     */
    public Task(TaskEnum taskType){
        this.taskType = taskType;
    }
}
