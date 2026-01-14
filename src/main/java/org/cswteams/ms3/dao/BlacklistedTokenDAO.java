package org.cswteams.ms3.dao;

import org.cswteams.ms3.entity.BlacklistedToken;
import org.cswteams.ms3.entity.SystemUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BlacklistedTokenDAO extends JpaRepository<BlacklistedToken, Long> {

    boolean existsByToken(String token);

    /**
     * Checks if a blacklisted token exists for a given system user's email, token, and a timestamp
     * after the specified date and time.
     *
     * @param systemUserEmail the email of the system user associated with the token
     * @param token           the token to be checked for blacklisting
     * @param referenceDate   the timestamp to compare against the blacklisted date and time
     * @return true if a blacklisted token matching the criteria exists, false otherwise
     */
    boolean existsBySystemUser_EmailAndTokenAndBlacklistedAtAfter(String systemUserEmail, String token, LocalDateTime referenceDate);

    void deleteByExpiresAtBefore(LocalDateTime expiresAtBefore);

}
