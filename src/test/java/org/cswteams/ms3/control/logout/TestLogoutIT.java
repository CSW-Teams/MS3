package org.cswteams.ms3.control.logout;

import org.cswteams.ms3.dao.BlacklistedTokenDAO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
class TestLogoutIT {

    @Autowired
    private ILogoutController logoutController;

    @Autowired
    private JwtBlacklistService jwtBlacklistService;

    @Autowired
    private BlacklistedTokenDAO blacklistedTokenDAO;

    @Test
    void testLogoutAndBlacklisting() {
        String token = "test-token";
        assertFalse(jwtBlacklistService.isBlacklisted(token));
        logoutController.logout(token);
        assertTrue(jwtBlacklistService.isBlacklisted(token));
        assertTrue(blacklistedTokenDAO.existsByToken(token));
    }

    @Test
    void testBlacklistIsPersistent() {
        String token = "test-token-2";
        assertFalse(jwtBlacklistService.isBlacklisted(token));
        jwtBlacklistService.blacklist(token);
        assertTrue(jwtBlacklistService.isBlacklisted(token));
        assertTrue(blacklistedTokenDAO.existsByToken(token));
    }

    @Test
    void testDoubleLogout() {
        String token = "test-token-3";
        assertFalse(jwtBlacklistService.isBlacklisted(token));
        logoutController.logout(token);
        assertTrue(jwtBlacklistService.isBlacklisted(token));
        logoutController.logout(token);
        assertTrue(jwtBlacklistService.isBlacklisted(token));
        assertTrue(blacklistedTokenDAO.existsByToken(token));
    }
}
