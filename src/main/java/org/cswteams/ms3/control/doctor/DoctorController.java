package org.cswteams.ms3.control.doctor;

import org.cswteams.ms3.dao.DoctorDAO;
import org.cswteams.ms3.dao.SpecializationDAO;
import org.cswteams.ms3.dto.medicalDoctor.MedicalDoctorInfoDTO;
import org.cswteams.ms3.dto.user.UserDTO;
import org.cswteams.ms3.entity.Doctor;
import org.cswteams.ms3.entity.Specialization;
import org.cswteams.ms3.entity.User;
import org.cswteams.ms3.enums.SystemActor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class DoctorController implements IDoctorController {

    @Autowired
    private DoctorDAO doctorDAO;
    @Autowired
    private SpecializationDAO specializationDAO;

    @Override
    public Set<MedicalDoctorInfoDTO> getAllDoctors() {

        List<Doctor> doctors = doctorDAO.findAll();
        Set<MedicalDoctorInfoDTO> doctorsSet = new HashSet<>();

        for (Doctor d: doctors){

            MedicalDoctorInfoDTO dto = new MedicalDoctorInfoDTO(
                    d.getId(),
                    d.getName(),
                    d.getLastname(),
                    d.getSeniority(),
                    ""
            );
            doctorsSet.add(dto);
        }

        return doctorsSet;
    }

    @Override
    public MedicalDoctorInfoDTO getDoctorById(Long id) {
        Optional<Doctor> doctor = doctorDAO.findById(id);
        return doctor.map(value -> new MedicalDoctorInfoDTO(value.getId(), value.getName(), value.getLastname(), value.getSeniority(), "")).orElse(null);
    }

    // TODO: Add checks on values
    @Override
    public void deleteDoctorSpecialization(Long doctorID, String specialization) {
        Doctor doctor = doctorDAO.findById((long) doctorID);
        Specialization dbSpecialization = specializationDAO.findByType(specialization);
        doctor.getSpecializations().remove(dbSpecialization);
        doctorDAO.saveAndFlush(doctor);
    }

    // TODO: Add checks on values
    @Override
    public void addDoctorSpecialization(Long doctorID, Set<String> specialization) {
        Doctor doctor = doctorDAO.findById((long) doctorID);
        for(String stringSpecialization: specialization){
            Specialization dbSpecialization = specializationDAO.findByType(stringSpecialization);
            if(!doctor.getSpecializations().contains(dbSpecialization))
                doctor.getSpecializations().add(dbSpecialization);
        }
        doctorDAO.saveAndFlush(doctor);
    }
}
