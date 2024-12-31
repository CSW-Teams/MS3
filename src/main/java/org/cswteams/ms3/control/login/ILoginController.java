package org.cswteams.ms3.control.login;

import org.cswteams.ms3.dto.login.CustomUserDetails;
import org.cswteams.ms3.enums.SystemActor;
import org.cswteams.ms3.exception.login.InvalidEmailAddressException;
import org.cswteams.ms3.exception.login.InvalidRoleException;

public interface ILoginController {

    /**
     * Authenticate a user, based on login data received via DTO.
     *
     * @return DTO with logged user data
     * @throws InvalidEmailAddressException if the email address provided within the DTO is invalid
     * @throws InvalidRoleException         if the logging user's specified role is invalid
     */
    CustomUserDetails authenticateUser(String email, String password, SystemActor systemActor) throws InvalidEmailAddressException, InvalidRoleException;

}
