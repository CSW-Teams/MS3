package org.cswteams.ms3.control.cambiaPassword;


import org.cswteams.ms3.dao.UtenteDao;
import org.cswteams.ms3.dto.PasswordDTO;
import org.cswteams.ms3.entity.doctor.Doctor;
import org.cswteams.ms3.exception.DatabaseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.util.Optional;

@Service
public class ControllerPassword implements IControllerPassword {
    @Autowired
    private  UtenteDao utenteDao;

    @Override
    public void cambiaPassword(@NotNull PasswordDTO dto) throws Exception {
        Optional<Doctor> u = utenteDao.findById(dto.getId());
        if (u == null) {
            throw new DatabaseException("utente non trovato");
        } else {
            Doctor doctor = u.get();
            if (doctor.getPassword().equals(dto.getOldPassword())) {
                doctor.setPassword(dto.getNewPassword());
                utenteDao.saveAndFlush(doctor);
            } else {
                throw new Exception();
            }
        }
    }
}
