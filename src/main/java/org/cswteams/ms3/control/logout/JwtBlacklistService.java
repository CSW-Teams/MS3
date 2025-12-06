package org.cswteams.ms3.control.logout;

import org.cswteams.ms3.dao.BlacklistedTokenDAO;
import org.cswteams.ms3.entity.BlacklistedToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class JwtBlacklistService {

    @Autowired
    private BlacklistedTokenDAO blacklistedTokenDAO;

    /**
     * Inserts the given JWT token into the blacklist.
     * If the token is already blacklisted, it does nothing.
     * @param token JWT token to be invalidated
     */
    public void blacklist(String token) {
        if (!blacklistedTokenDAO.existsByToken(token)) {
            BlacklistedToken blacklistedToken = new BlacklistedToken();
            blacklistedToken.setToken(token);
            blacklistedToken.setBlacklistedAt(LocalDateTime.now());
            blacklistedTokenDAO.save(blacklistedToken);
        }
    }

    /**
     * Checks whether the given JWT token has been blacklisted.
     *
     * @param token JWT token
     * @return true if the token is blacklisted, false otherwise
     */
    public boolean isBlacklisted(String token) {
        return blacklistedTokenDAO.existsByToken(token);
    }
}
