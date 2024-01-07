package org.cswteams.ms3.control.task;

import org.cswteams.ms3.entity.Doctor;
import org.cswteams.ms3.entity.MedicalService;
import org.cswteams.ms3.entity.Task;
import org.cswteams.ms3.enums.TaskEnum;


public interface ITaskController {
/*
    Set<RotationDTO> leggiTurniDiServizio(String servizio);

    Set<RotationDTO> leggiTurni();

    Shift creaTurno(RotationDTO turno) throws ShiftException;*/

    void assignDoctorToTask(Task task, Doctor doctor);

    void assignDoctorToMedicalService(MedicalService medicalService, Doctor doctor);

    /**
     * Create a <code>Task</code> with only the task type, without assigning (yet) any <code>Doctor</code>.
     * Useful when creating a new medical service.
     *
     * @param taskType task type
     * @return new task, with <code>taskType</code> as task type.
     */
    Task createTaskWithType(TaskEnum taskType);
}
