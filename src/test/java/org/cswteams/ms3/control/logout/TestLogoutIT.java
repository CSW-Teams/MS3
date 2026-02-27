package org.cswteams.ms3.control.logout;

import org.cswteams.ms3.dao.BlacklistedTokenDAO;
import org.cswteams.ms3.dao.SystemUserDAO;
import org.cswteams.ms3.entity.SystemUser;
import org.cswteams.ms3.utils.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@SpringBootTest
@Transactional
class TestLogoutIT {

    @Autowired
    private ILogoutController logoutController;

    @Autowired
    private JwtBlacklistService jwtBlacklistService;

    @Autowired
    private BlacklistedTokenDAO blacklistedTokenDAO;

    @Autowired
    private SystemUserDAO userDAO;

    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private Clock clock;

    private SystemUser testUser;

    @BeforeEach
    void setup() {
        // Non-trivial fixture: freeze security-related collaborators so blacklist timestamps and expiration reads are deterministic.
        when(clock.getZone()).thenReturn(ZoneId.systemDefault());
        when(clock.instant()).thenReturn(Instant.now());
        when(jwtUtil.extractExpiration(anyString())).thenReturn(new Date());

        // Reuse a real persisted user so logout integration exercises DAO + service layers end-to-end.
        testUser = userDAO.findAll().get(0);
    }

    @Test
    void testLogoutAndBlacklisting() {
        // Given an active token, when logout endpoint logic runs, then the same token must become blacklisted and persisted.
        // Regression guard: catches flows where logout returns success but token remains accepted.
        String token = "test-token";

        assertFalse(jwtBlacklistService.isBlacklisted(token));

        logoutController.logout(token, testUser.getEmail());

        assertTrue(jwtBlacklistService.isBlacklisted(token));
        assertTrue(blacklistedTokenDAO.existsByToken(token));
    }

    @Test
    void testBlacklistIsPersistent() {
        // Given direct blacklisting, when querying afterward, then persistence must survive and report token as revoked.
        // Regression guard: prevents token revocation from being kept only in-memory.
        String token = "test-token-2";

        assertFalse(jwtBlacklistService.isBlacklisted(token));

        jwtBlacklistService.blacklist(token, testUser);

        assertTrue(jwtBlacklistService.isBlacklisted(token));
        assertTrue(blacklistedTokenDAO.existsByToken(token));
    }

    @Test
    void testDoubleLogout() {
        // Given an already-logged-out token, when logout is retried, then revocation must remain effective (idempotent behavior).
        // Regression guard: protects against second logout accidentally re-enabling a revoked token.
        String token = "test-token-3";

        logoutController.logout(token, testUser.getEmail());
        assertTrue(jwtBlacklistService.isBlacklisted(token));

        logoutController.logout(token, testUser.getEmail());

        assertTrue(jwtBlacklistService.isBlacklisted(token));
        assertTrue(blacklistedTokenDAO.existsByToken(token));
    }
}
