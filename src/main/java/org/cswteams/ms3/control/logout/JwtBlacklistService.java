package org.cswteams.ms3.control.logout;

import lombok.extern.slf4j.Slf4j;
import org.cswteams.ms3.dao.BlacklistedTokenDAO;
import org.cswteams.ms3.entity.BlacklistedToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.LocalDateTime;

@Service
@Slf4j
public class JwtBlacklistService {

    private final BlacklistedTokenDAO blacklistedTokenDAO;
    private final Clock clock;

    @Autowired
    public JwtBlacklistService(BlacklistedTokenDAO blacklistedTokenDAO, Clock clock) {
        this.blacklistedTokenDAO = blacklistedTokenDAO;
        this.clock = clock;
    }

    /**
     * Inserts the given JWT token into the blacklist.
     * If the token is already blacklisted, it does nothing.
     *
     * @param token JWT token to be invalidated
     */
    public void blacklist(String token) {
        try {
            if (!isBlacklisted(token)) {
                BlacklistedToken blacklistedToken = new BlacklistedToken();
                blacklistedToken.setToken(token);
                blacklistedToken.setBlacklistedAt(LocalDateTime.now(clock));
                blacklistedTokenDAO.save(blacklistedToken);
            }
        } catch (IllegalArgumentException e) {
            log.error("Failed to blacklist token", e);
        }
    }

    /**
     * Checks whether the given JWT token has been blacklisted.
     *
     * @param token JWT token
     * @return true if the token is blacklisted, false otherwise or if the token is null or empty
     */
    public boolean isBlacklisted(String token) throws IllegalArgumentException {
        if (token == null || token.isEmpty()) throw new IllegalArgumentException("Token cannot be null or empty");
        return blacklistedTokenDAO.existsByToken(token);
    }
}
