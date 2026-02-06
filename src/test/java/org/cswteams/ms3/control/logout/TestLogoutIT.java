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
        // 1. Mock dependencies
        when(clock.getZone()).thenReturn(ZoneId.systemDefault());
        when(clock.instant()).thenReturn(Instant.now());
        when(jwtUtil.extractExpiration(anyString())).thenReturn(new Date());

        // 2. Instantiate using the private subclass
        testUser = userDAO.findAll().get(0);
    }

    @Test
    void testLogoutAndBlacklisting() {
        String token = "test-token";

        assertFalse(jwtBlacklistService.isBlacklisted(token));

        logoutController.logout(token, testUser.getEmail());

        assertTrue(jwtBlacklistService.isBlacklisted(token));
        assertTrue(blacklistedTokenDAO.existsByToken(token));
    }

    @Test
    void testBlacklistIsPersistent() {
        String token = "test-token-2";

        assertFalse(jwtBlacklistService.isBlacklisted(token));

        jwtBlacklistService.blacklist(token, testUser);

        assertTrue(jwtBlacklistService.isBlacklisted(token));
        assertTrue(blacklistedTokenDAO.existsByToken(token));
    }

    @Test
    void testDoubleLogout() {
        String token = "test-token-3";

        logoutController.logout(token, testUser.getEmail());
        assertTrue(jwtBlacklistService.isBlacklisted(token));

        logoutController.logout(token, testUser.getEmail());

        assertTrue(jwtBlacklistService.isBlacklisted(token));
        assertTrue(blacklistedTokenDAO.existsByToken(token));
    }
}