package org.cswteams.ms3.control.cambiaPassword;

import org.cswteams.ms3.dto.PasswordDTO;

public interface IControllerPassword {

    void cambiaPassword(PasswordDTO dto) throws Exception;
}
