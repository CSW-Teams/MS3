package org.cswteams.ms3.control.logout;

import lombok.extern.slf4j.Slf4j;
import org.cswteams.ms3.dao.BlacklistedTokenDAO;
import org.cswteams.ms3.entity.BlacklistedToken;
import org.cswteams.ms3.entity.SystemUser;
import org.cswteams.ms3.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Date;

@Service
@Slf4j
public class JwtBlacklistService {

    private final static String WILDCARD_TOKEN = "*";

    private final BlacklistedTokenDAO blacklistedTokenDAO;
    private final JwtUtil jwtUtil;
    private final Clock clock;

    @Autowired
    public JwtBlacklistService(BlacklistedTokenDAO blacklistedTokenDAO, JwtUtil jwtUtil, Clock clock) {
        this.blacklistedTokenDAO = blacklistedTokenDAO;
        this.jwtUtil = jwtUtil;
        this.clock = clock;
    }

    /**
     * Adds the specified token to the blacklist to prevent its future use.
     * If the token is not already blacklisted, it is stored with the current timestamp.
     *
     * @param token      the JWT token to be blacklisted
     * @param systemUser the user associated with the token
     */
    public void blacklist(String token, SystemUser systemUser) {
        try {
            if (!isBlacklisted(token) && systemUser != null) {
                BlacklistedToken blacklistedToken = new BlacklistedToken();
                blacklistedToken.setToken(token);
                blacklistedToken.setBlacklistedAt(LocalDateTime.now(clock));
                blacklistedToken.setSystemUser(systemUser);

                // The expiration date claim is extracted as a Date object and must be converted to LocalDateTime
                // The following approach assures keeping the system timezone during the conversion, avoiding any time shift
                Date expirationDate = jwtUtil.extractExpiration(token);
                LocalDateTime localDateTime = dateToLocalDateTime(expirationDate);
                blacklistedToken.setExpiresAt(localDateTime);

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

    /**
     * Checks if a user has any blacklisted wildcard tokens after a specified date.
     * A wildcard token, represented by a predefined value, is used to indicate
     * that all tokens for a user have been invalidated after the specified date.
     *
     * @param userEmail      the email of the user for whom the check is performed; must not be null or empty
     * @param tokenIssueDate the date to check against; wildcard tokens blacklisted after this date will be considered
     * @return true if the user has blacklisted wildcard tokens after the specified date, false otherwise
     * @throws IllegalArgumentException if the userEmail is null or empty
     */
    public boolean doesUserHaveTokensBlacklistedAfterDate(String userEmail, LocalDateTime tokenIssueDate) throws IllegalArgumentException {
        if (userEmail == null || userEmail.isEmpty())
            throw new IllegalArgumentException("User email cannot be null or empty");
        return blacklistedTokenDAO.existsBySystemUser_EmailAndTokenAndBlacklistedAtAfter(userEmail, WILDCARD_TOKEN, tokenIssueDate);
    }

    public boolean doesUserHaveTokensBlacklistedAfterDate(String userEmail, Date tokenIssueDate) throws IllegalArgumentException {
        if (tokenIssueDate == null) throw new IllegalArgumentException("Token issue date cannot be null");
        return doesUserHaveTokensBlacklistedAfterDate(userEmail, dateToLocalDateTime(tokenIssueDate));
    }

    /**
     * Blacklists all JWT tokens associated with the specified user, preventing future use.
     * Since the system has no trace of the issued tokens, this functionality is implemented
     * by removing all the previously issued tokens and saving a "wildcard" token associated with
     * the user. The maximum of the tokens' expiration dates is used as the expiration date of the wildcard token.
     *
     * @param systemUser the user whose tokens are to be blacklisted
     */
    public void blacklistAllUserTokens(SystemUser systemUser) {
        if (systemUser == null) throw new IllegalArgumentException("User cannot be null");
        LocalDateTime now = LocalDateTime.now(clock);
        // Set the expiration date to 1 hour after the current time
        LocalDateTime maxExpirationDate = now.plusHours(1);
        BlacklistedToken wildcardToken = new BlacklistedToken();
        wildcardToken.setToken("*");
        wildcardToken.setBlacklistedAt(now);
        wildcardToken.setSystemUser(systemUser);
        wildcardToken.setExpiresAt(maxExpirationDate);
        blacklistedTokenDAO.save(wildcardToken);
    }

    /**
     * Converts a given {@link Date} object to a {@link LocalDateTime} object.
     * The conversion ensures that the system's current timezone is maintained.
     *
     * @param date the {@link Date} object to be converted; must not be null
     * @return the equivalent {@link LocalDateTime} representation of the given date
     * @throws NullPointerException if the provided date is null
     */
    private LocalDateTime dateToLocalDateTime(Date date) {
        if (date == null) throw new IllegalArgumentException("Provided date cannot be null");
        return LocalDateTime.ofInstant(date.toInstant(), clock.getZone());
    }
}
