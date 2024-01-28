package org.cswteams.ms3.control.registrazione;

import org.cswteams.ms3.dto.registration.RegisteredUserDTO;
import org.cswteams.ms3.dto.registration.RegistrationDTO;
import org.cswteams.ms3.exception.registration.RegistrationException;

public interface IControllerRegistrazione {

    RegisteredUserDTO registerUser(RegistrationDTO registrationDTO) throws RegistrationException;
}
