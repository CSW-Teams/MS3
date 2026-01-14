package org.cswteams.ms3.control.logout;

import lombok.extern.slf4j.Slf4j;
import org.cswteams.ms3.dao.SystemUserDAO;
import org.cswteams.ms3.entity.SystemUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class LogoutController implements ILogoutController {

    private final JwtBlacklistService blacklistService;

    private final SystemUserDAO userDAO;

    @Autowired
    public LogoutController(JwtBlacklistService blacklistService, SystemUserDAO userDAO) {
        this.blacklistService = blacklistService;
        this.userDAO = userDAO;
    }

    /**
     * Logs out a user by invalidating the specified JWT token. If the provided token is not empty or null,
     * the method fetches the associated user using the given email, which must also be non-null and non-empty.
     * If the user is found, the token is added to a blacklist to prevent any further use.
     *
     * @param token     the JWT token to be invalidated. Must not be null or empty.
     * @param userEmail the email address of the user to be logged out. This email is used to fetch the user
     *                  associated with the token. If the user is not found, a {@link UsernameNotFoundException}
     *                  is thrown.
     * @throws UsernameNotFoundException if no user is found associated with the given email address.
     */
    @Override
    public void logout(String token, String userEmail) throws UsernameNotFoundException {
        if (token != null && !token.isEmpty() && userEmail != null && !userEmail.isEmpty()) {
            SystemUser systemUser = userDAO.findByEmail(userEmail);
            if (systemUser == null) {
                log.error("TenantUser not found with email: {}", userEmail);
                throw new UsernameNotFoundException("TenantUser not found with email: " + userEmail);
            }
            blacklistService.blacklist(token, systemUser);
        }
    }
}

