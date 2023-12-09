package org.cswteams.ms3.control.login;


import org.cswteams.ms3.control.utils.MappaUtenti;
import org.cswteams.ms3.dao.UtenteDao;
import org.cswteams.ms3.dto.LoginDTO;
import org.cswteams.ms3.dto.UtenteDTO;
import org.cswteams.ms3.entity.doctor.Doctor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;

@Service
public class ControllerLogin implements IControllerLogin {
    @Autowired
    private  UtenteDao utenteDao;


    @Override
    public UtenteDTO autenticaUtente(@NotNull LoginDTO loginDTO) {
        Doctor u = utenteDao.findByEmailAndPassword(loginDTO.getUsername(), loginDTO.getPassword());
        UtenteDTO dto = null;
        if (u != null)
            dto = MappaUtenti.utenteEntitytoDTO(u);

        return dto;
    }
}
