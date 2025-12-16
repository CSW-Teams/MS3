package org.cswteams.ms3.control.logout;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class LogoutController implements ILogoutController {

    private final JwtBlacklistService blacklistService;

    public LogoutController(@Autowired JwtBlacklistService blacklistService) {
        this.blacklistService = blacklistService;
    }

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

