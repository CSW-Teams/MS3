package org.cswteams.ms3.control.login;

import org.cswteams.ms3.dto.login.LoggedUserDTO;
import org.cswteams.ms3.dto.login.LoginDTO;
import org.cswteams.ms3.exception.login.InvalidEmailAddressException;
import org.cswteams.ms3.exception.login.InvalidRoleException;

public interface IControllerLogin {

    /**
     * Authenticate a user, based on login data received via DTO.
     *
     * @param loginDTO login data
     * @return DTO with logged user data
     * @throws InvalidEmailAddressException if the email address provided within the DTO is invalid
     * @throws InvalidRoleException         if the logging user's specified role is invalid
     */
    LoggedUserDTO authenticateUser(LoginDTO loginDTO) throws InvalidEmailAddressException, InvalidRoleException;

}
