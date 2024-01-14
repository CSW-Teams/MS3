package org.cswteams.ms3.control.doctor;

import org.cswteams.ms3.dto.medicalDoctor.MedicalDoctorInfoDTO;

import java.util.Set;

public interface IDoctorController {

    Set<MedicalDoctorInfoDTO> getAllDoctors();
    MedicalDoctorInfoDTO getDoctorById(Long id);

}
