package org.cswteams.ms3.control.login;

import org.cswteams.ms3.dto.login.LoggedUserDTO;
import org.cswteams.ms3.dto.login.LoginDTO;
import org.cswteams.ms3.exception.login.InvalidEmailAddressException;
import org.cswteams.ms3.exception.login.InvalidRoleException;

public interface IControllerLogin {

    LoggedUserDTO authenticateUser(LoginDTO loginDTO) throws InvalidEmailAddressException, InvalidRoleException;

}
