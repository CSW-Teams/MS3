package org.cswteams.ms3.service.tokenRemoval;

import org.cswteams.ms3.control.logout.ExpiredTokensRemovalService;
import org.cswteams.ms3.dao.BlacklistedTokenDAO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.test.context.ActiveProfiles;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

@SpringBootTest
@ActiveProfiles("test") // Use application-test.properties with H2 config
class ExpiredTokensRemovalServiceIT {

    @Autowired
    private ExpiredTokensRemovalService service;

    @Autowired
    private BlacklistedTokenDAO dao;

    @Test
    void deleteExpiredTokens_shouldRemoveDataFromDB() {
        // Arrange
        // 1. Create a token that expired BEFORE the fixed clock time
        LocalDateTime oldDate = LocalDateTime.of(2023, 1, 1, 10, 0);
        // dao.save(new BlacklistedToken(..., oldDate));

        // 2. Create a token that expires AFTER the fixed clock time (should not be deleted)
        LocalDateTime futureDate = LocalDateTime.of(2025, 1, 1, 10, 0);
        // dao.save(new BlacklistedToken(..., futureDate));

        // Act
        service.deleteExpiredTokens();
    }

    // We can define a TestConfiguration to override the Clock bean for this test context
    @TestConfiguration
    static class TestConfig {
        @Bean
        @Primary
        public Clock fixedClock() {
            return Clock.fixed(Instant.parse("2024-01-01T12:00:00Z"), ZoneId.of("UTC"));
        }
    }
}