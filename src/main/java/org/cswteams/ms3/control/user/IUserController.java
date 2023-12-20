package org.cswteams.ms3.control.user;

import org.cswteams.ms3.dto.DoctorDTO;

import java.util.Set;


public interface IUserController {

    Set<DoctorDTO> getAllUsers();

    Object createUser(DoctorDTO c);

    DoctorDTO getSingleUser(long idUtente);

}
