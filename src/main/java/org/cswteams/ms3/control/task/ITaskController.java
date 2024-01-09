package org.cswteams.ms3.control.task;

import org.cswteams.ms3.entity.Task;
import org.cswteams.ms3.enums.TaskEnum;


public interface ITaskController {

    /**
     * Create a <code>Task</code> with the specified task type.
     *
     * @param taskType task type
     * @return new task, with <code>taskType</code> as task type.
     */
    Task createTask(TaskEnum taskType);
}
