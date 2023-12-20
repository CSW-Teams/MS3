package org.cswteams.ms3.control.login;

import org.cswteams.ms3.dto.LoginDTO;
import org.cswteams.ms3.dto.DoctorDTO;

public interface IControllerLogin {

    DoctorDTO authenticateUser(LoginDTO loginDTO);

}
