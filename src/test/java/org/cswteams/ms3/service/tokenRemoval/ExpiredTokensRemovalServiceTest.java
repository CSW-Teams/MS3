package org.cswteams.ms3.service.tokenRemoval;

import org.cswteams.ms3.control.logout.ExpiredTokensRemovalService;
import org.cswteams.ms3.dao.BlacklistedTokenDAO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ExpiredTokensRemovalServiceTest {

    @Mock
    private BlacklistedTokenDAO dao;

    private ExpiredTokensRemovalService service;

    // Define a fixed time for testing
    private final Instant FIXED_INSTANT = Instant.parse("2024-01-01T12:00:00Z");
    private final ZoneId ZONE = ZoneId.of("UTC");

    @BeforeEach
    void setUp() {
        // Create a clock that is frozen at a specific time
        Clock fixedClock = Clock.fixed(FIXED_INSTANT, ZONE);
        service = new ExpiredTokensRemovalService(dao, fixedClock);
    }

    @Test
    void deleteExpiredTokens_shouldCallDaoWithCorrectTimestamp() {
        // Act
        // We call the method directly to test the logic, ignoring the @Scheduled trigger
        service.deleteExpiredTokens();

        // Assert
        // Verify the DAO was called with the exact time defined by our fixed clock
        LocalDateTime expectedTime = LocalDateTime.ofInstant(FIXED_INSTANT, ZONE);
        verify(dao).deleteByExpiresAtBefore(expectedTime);
    }
}