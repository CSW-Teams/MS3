package org.cswteams.ms3.control.doctor;

import org.cswteams.ms3.dto.condition.UpdateConditionsDTO;
import org.cswteams.ms3.dto.medicalDoctor.MedicalDoctorInfoDTO;

import java.util.Set;

public interface IDoctorController {

    Set<MedicalDoctorInfoDTO> getAllDoctors();
    MedicalDoctorInfoDTO getDoctorById(Long id);

    void deleteDoctorSpecialization(Long doctorID, String specialization);

    void addDoctorSpecialization(Long doctorID, Set<String> specialization);

    void deleteDoctorPermanentCondition(Long doctorID, Long conditionID, String condition);

    void deleteDoctorTemporaryCondition(Long doctorID, Long conditionID, String condition);


    long addDoctorCondition(Long doctorID, UpdateConditionsDTO.GenericCondition condition);
}
