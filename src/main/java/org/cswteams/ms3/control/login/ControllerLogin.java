package org.cswteams.ms3.control.login;


import org.cswteams.ms3.control.utils.MappaUtenti;
import org.cswteams.ms3.dao.UtenteDao;
import org.cswteams.ms3.dto.DoctorDTO;
import org.cswteams.ms3.dto.LoginDTO;
import org.cswteams.ms3.entity.doctor.Doctor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;

@Service
public class ControllerLogin implements IControllerLogin {
    @Autowired
    private  UtenteDao utenteDao;


    @Override
    public DoctorDTO autenticaUtente(@NotNull LoginDTO loginDTO) {
        Doctor u = utenteDao.findByEmailAndPassword(loginDTO.getUsername(), loginDTO.getPassword());
        DoctorDTO dto = null;
        if (u != null)
            dto = MappaUtenti.utenteEntityToDTO(u);

        return dto;
    }
}
