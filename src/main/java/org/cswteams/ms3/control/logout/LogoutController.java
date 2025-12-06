package org.cswteams.ms3.control.logout;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class LogoutController implements ILogoutController {

    @Autowired
    private JwtBlacklistService blacklistService;

    /**
     * Performs logout by blacklisting the given JWT token.
     */
    @Override
    public void logout(String token) {
        if (token != null && !token.isEmpty()) {
            blacklistService.blacklist(token);
        }
    }
}

