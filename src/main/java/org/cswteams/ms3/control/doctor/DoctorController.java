package org.cswteams.ms3.control.doctor;

import org.cswteams.ms3.dao.DoctorDAO;
import org.cswteams.ms3.dto.medicalDoctor.MedicalDoctorInfoDTO;
import org.cswteams.ms3.dto.user.UserDTO;
import org.cswteams.ms3.entity.Doctor;
import org.cswteams.ms3.entity.User;
import org.cswteams.ms3.enums.SystemActor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class DoctorController implements IDoctorController {

    @Autowired
    private DoctorDAO doctorDAO;

    @Override
    public Set<MedicalDoctorInfoDTO> getAllDoctors() {

        List<Doctor> doctors = doctorDAO.findAll();
        Set<MedicalDoctorInfoDTO> doctorsSet = new HashSet<>();

        for (Doctor d: doctors){

            MedicalDoctorInfoDTO dto = new MedicalDoctorInfoDTO(
                    d.getId(),
                    d.getName(),
                    d.getLastname(),
                    d.getSeniority()
            );
            doctorsSet.add(dto);
        }

        return doctorsSet;
    }
}
