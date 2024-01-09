package org.cswteams.ms3.control.task;

import org.cswteams.ms3.dao.TaskDAO;
import org.cswteams.ms3.entity.Task;
import org.cswteams.ms3.enums.TaskEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TaskController implements ITaskController {

    @Autowired
    TaskDAO taskDAO;

    @Override
    public Task createTask(TaskEnum taskType) {
        Task t = new Task(taskType);
        taskDAO.saveAndFlush(t);
        return t;
    }
}
