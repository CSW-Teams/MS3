package org.cswteams.ms3.control.task;

import org.cswteams.ms3.entity.Doctor;
import org.cswteams.ms3.entity.MedicalService;
import org.cswteams.ms3.entity.Task;


public interface ITaskController {
/*
    Set<RotationDTO> leggiTurniDiServizio(String servizio);

    Set<RotationDTO> leggiTurni();

    Shift creaTurno(RotationDTO turno) throws ShiftException;*/

    void addDoctor(Task task, Doctor doctor);

    void addService(MedicalService medicalService, Doctor doctor);
}
