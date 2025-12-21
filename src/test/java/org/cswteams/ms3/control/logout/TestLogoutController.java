package org.cswteams.ms3.control.logout;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.*;

class TestLogoutController {

    @Mock
    private JwtBlacklistService blacklistService;

    @InjectMocks
    private LogoutController logoutController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void testLogout_WithNullToken_NoCalls() {
        logoutController.logout(null);
        verify(blacklistService, never()).blacklist(anyString());
    }

    @Test
    void testLogout_WithEmptyToken_NoCalls() {
        logoutController.logout("");
        verify(blacklistService, never()).blacklist(anyString());
    }

    @Test
    void testLogout_WithValidToken_TokenBlacklisted() {
        String token = "valid_token";
        doNothing().when(blacklistService).blacklist(token);
        logoutController.logout(token);
        verify(blacklistService, times(1)).blacklist(token);
    }
}
