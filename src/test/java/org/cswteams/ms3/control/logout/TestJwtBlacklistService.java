package org.cswteams.ms3.control.logout;

import org.cswteams.ms3.dao.BlacklistedTokenDAO;
import org.cswteams.ms3.entity.BlacklistedToken;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneId;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class TestJwtBlacklistService {

    @Mock
    private BlacklistedTokenDAO blacklistedTokenDAO;

    @Mock
    private Clock clock;

    @InjectMocks
    private JwtBlacklistService jwtBlacklistService;

    // Fixed the clock for consistent time in tests
    private LocalDateTime fixedDateTime;

    @BeforeEach
    void setUp() {
        // Initialize Mockito mocks
        MockitoAnnotations.initMocks(this);

        // Set up a fixed clock
        fixedDateTime = LocalDateTime.of(2025, 12, 16, 10, 0, 0);
        when(clock.getZone()).thenReturn(ZoneId.systemDefault());
        when(clock.instant()).thenReturn(fixedDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

    @Test
    void testBlacklist_NewToken_Blacklist() {
        String token = "new_token";
        when(blacklistedTokenDAO.existsByToken(token)).thenReturn(false);

        jwtBlacklistService.blacklist(token);

        ArgumentCaptor<BlacklistedToken> argument = ArgumentCaptor.forClass(BlacklistedToken.class);
        verify(blacklistedTokenDAO, times(1)).save(argument.capture());
        BlacklistedToken savedToken = argument.getValue();

        assertEquals(token, savedToken.getToken());
        assertEquals(fixedDateTime, savedToken.getBlacklistedAt());
    }

    @Test
    void testBlacklist_ExistingToken_NoBlacklist() {
        String token = "existing_token";
        when(blacklistedTokenDAO.existsByToken(token)).thenReturn(true);
        jwtBlacklistService.blacklist(token);
        verify(blacklistedTokenDAO, never()).save(any(BlacklistedToken.class));
    }

    @Test
    void testBlacklist_NullToken_NoCalls() {
        jwtBlacklistService.blacklist(null);
        verify(blacklistedTokenDAO, never()).save(any(BlacklistedToken.class));
    }

    @Test
    void testBlacklist_EmptyToken_NoCalls() {
        jwtBlacklistService.blacklist("");
        verify(blacklistedTokenDAO, never()).save(any(BlacklistedToken.class));
    }

    @Test
    void testIsBlacklisted_BlacklistedToken_ReturnsTrue() {
        String token = "blacklisted_token";
        when(blacklistedTokenDAO.existsByToken(token)).thenReturn(true);
        assertTrue(jwtBlacklistService.isBlacklisted(token));
    }

    @Test
    void testIsBlacklisted_NotBlacklistedToken_ReturnsFalse() {
        String token = "not_blacklisted_token";
        when(blacklistedTokenDAO.existsByToken(token)).thenReturn(false);
        assertFalse(jwtBlacklistService.isBlacklisted(token));
    }

    @Test
    void testIsBlacklisted_NullToken_ThrowsException() {
        IllegalArgumentException exc = assertThrows(IllegalArgumentException.class, () -> jwtBlacklistService.isBlacklisted(null));
        assertEquals("Token cannot be null or empty", exc.getMessage());
    }

    @Test
    void testIsBlacklisted_EmptyToken_ThrowsException() {
        IllegalArgumentException exc = assertThrows(IllegalArgumentException.class, () -> jwtBlacklistService.isBlacklisted(""));
        assertEquals("Token cannot be null or empty", exc.getMessage());
    }
}
