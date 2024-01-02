package org.cswteams.ms3.control.passwordChange;

import org.cswteams.ms3.dto.changePassword.ChangePasswordDTO;
import org.cswteams.ms3.exception.DatabaseException;
import org.cswteams.ms3.exception.changePassword.WrongOldPasswordException;

public interface IPasswordChange {

    ChangePasswordDTO changePassword(ChangePasswordDTO dto) throws DatabaseException, WrongOldPasswordException;
}
