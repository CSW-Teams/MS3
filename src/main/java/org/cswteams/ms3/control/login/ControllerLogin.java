package org.cswteams.ms3.control.login;


import org.cswteams.ms3.dao.DoctorDAO;
import org.cswteams.ms3.dto.DoctorDTO;
import org.cswteams.ms3.dto.LoginDTO;
import org.cswteams.ms3.entity.Doctor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;

@Service
public class ControllerLogin implements IControllerLogin {
    @Autowired
    private DoctorDAO doctorDAO;


    @Override
    public DoctorDTO authenticateUser(@NotNull LoginDTO loginDTO) {
        Doctor doctor = doctorDAO.findByEmailAndPassword(loginDTO.getEmail(), loginDTO.getPassword());
        DoctorDTO dto = null;
        if (doctor != null){
            dto = new DoctorDTO(doctor.getId(), doctor.getName(), doctor.getLastname(), doctor.getSeniority());
        }
        return dto;
    }
}
