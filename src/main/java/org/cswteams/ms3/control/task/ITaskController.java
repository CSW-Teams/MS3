package org.cswteams.ms3.control.task;

import org.cswteams.ms3.entity.Task;
import org.cswteams.ms3.enums.TaskEnum;


public interface ITaskController {
/*
    Set<RotationDTO> leggiTurniDiServizio(String servizio);

    Set<RotationDTO> leggiTurni();

    Shift creaTurno(RotationDTO turno) throws ShiftException;*/

    /**
     * Create a <code>Task</code> with the specified task type.
     *
     * @param taskType task type
     * @return new task, with <code>taskType</code> as task type.
     */
    Task createTask(TaskEnum taskType);
}
