package org.cswteams.ms3.control.passwordChange;


import org.cswteams.ms3.control.logout.JwtBlacklistService;
import org.cswteams.ms3.dao.SystemUserDAO;
import org.cswteams.ms3.dao.TenantUserDAO;
import org.cswteams.ms3.dto.changePassword.ChangePasswordDTO;
import org.cswteams.ms3.entity.TenantUser;
import org.cswteams.ms3.exception.DatabaseException;
import org.cswteams.ms3.exception.changePassword.WrongOldPasswordException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.util.Optional;

@Service
public class PasswordChange implements IPasswordChange {

    private final JwtBlacklistService blacklistService;
    private final TenantUserDAO userDAO;
    private final SystemUserDAO systemUserDAO;

    @Autowired
    public PasswordChange(JwtBlacklistService blacklistService, TenantUserDAO userDAO, SystemUserDAO systemUserDAO) {
        this.blacklistService = blacklistService;
        this.userDAO = userDAO;
        this.systemUserDAO = systemUserDAO;
    }

    @Override
    public ChangePasswordDTO changePassword(@NotNull ChangePasswordDTO dto) throws DatabaseException, WrongOldPasswordException {
        Optional<TenantUser> u = userDAO.findById(dto.getUserId());
        if (u.isEmpty()) {
            throw new DatabaseException("TenantUser not found.");
        } else {
            TenantUser user = u.get();
            if (user.getPassword().equals(dto.getOldPassword())) {
                user.setPassword(dto.getNewPassword());
                userDAO.saveAndFlush(user);
                // Invalidates all the tokens of the user
                blacklistService.blacklistAllUserTokens(systemUserDAO.findByEmail(user.getEmail()));
                return new ChangePasswordDTO(user.getId(), dto.getOldPassword(), user.getPassword());
            } else {
                throw new WrongOldPasswordException("The old password is wrong.");
            }
        }
    }
}
