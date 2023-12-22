package org.cswteams.ms3.control.passwordChange;


import org.cswteams.ms3.dao.DoctorDAO;
import org.cswteams.ms3.dto.PasswordDTO;
import org.cswteams.ms3.entity.Doctor;
import org.cswteams.ms3.exception.DatabaseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.util.Optional;

@Service
public class PasswordChange implements IPasswordChange {
    @Autowired
    private DoctorDAO doctorDao;

    @Override
    public void changePassword(@NotNull PasswordDTO dto) throws Exception {
        Optional<Doctor> u = doctorDao.findById(dto.getId());
        if (u == null) {
            throw new DatabaseException("utente non trovato");
        } else {
            Doctor doctor = u.get();
            if (doctor.getPassword().equals(dto.getOldPassword())) {
                doctor.setPassword(dto.getNewPassword());
                doctorDao.saveAndFlush(doctor);
            } else {
                throw new Exception();
            }
        }
    }
}
