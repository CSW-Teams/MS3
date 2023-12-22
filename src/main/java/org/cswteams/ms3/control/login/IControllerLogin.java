package org.cswteams.ms3.control.login;

import org.cswteams.ms3.dto.login.LoggedUserDTO;
import org.cswteams.ms3.dto.login.LoginDTO;
import org.cswteams.ms3.dto.DoctorDTO;
import org.cswteams.ms3.exception.login.InvalidEmailAddressException;

public interface IControllerLogin {

    LoggedUserDTO authenticateUser(LoginDTO loginDTO) throws InvalidEmailAddressException;

}
