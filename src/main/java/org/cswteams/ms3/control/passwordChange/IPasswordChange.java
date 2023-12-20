package org.cswteams.ms3.control.passwordChange;

import org.cswteams.ms3.dto.PasswordDTO;

public interface IPasswordChange {

    void changePassword(PasswordDTO dto) throws Exception;
}
