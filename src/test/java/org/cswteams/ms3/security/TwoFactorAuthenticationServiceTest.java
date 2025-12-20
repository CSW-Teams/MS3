package org.cswteams.ms3.security;

import org.cswteams.ms3.dao.SystemUserDAO;
import org.cswteams.ms3.entity.SystemUser;
import org.cswteams.ms3.enums.SystemActor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
class TwoFactorAuthenticationServiceTest {

    @Mock
    private SystemUserDAO systemUserDAO;

    private TwoFactorProperties properties;
    private TwoFactorCodeService codeService;
    private TwoFactorAuthenticationService service;
    private MutableClock mutableClock;

    @BeforeEach
    void setUp() {
        properties = new TwoFactorProperties();
        properties.setRequiredRoles(List.of(SystemActor.PLANNER));
        properties.setMasterKey("unit-test-master-key");
        properties.setAllowedDriftWindows(0);
        properties.setLockoutThreshold(2);
        properties.setRecoveryCodeCount(5);
        properties.setRecoveryCodeLength(12);

        mutableClock = new MutableClock(Instant.parse("2024-01-01T00:00:00Z"));
        codeService = new TwoFactorCodeService(properties, mutableClock);
        service = new TwoFactorAuthenticationService(properties, codeService, systemUserDAO, mutableClock);
    }

    @Test
    void requiredRoleWithoutCodeShouldChallenge() {
        SystemUser user = buildUser(Set.of(SystemActor.PLANNER), true);

        TwoFactorResult result = service.processTwoFactor(user, null);

        assertTrue(result.isRequiresTwoFactor());
        assertFalse(result.isSuccessful());
        assertEquals(HttpStatus.FORBIDDEN, result.getStatus());
        verifyNoInteractions(systemUserDAO);
    }

    @Test
    void requiredRoleWithValidCodeShouldSucceed() {
        SystemUser user = buildUser(Set.of(SystemActor.PLANNER), true);
        String otp = codeService.currentCodeForUser(user);

        TwoFactorResult result = service.processTwoFactor(user, otp);

        assertTrue(result.isRequiresTwoFactor());
        assertTrue(result.isSuccessful());
        verify(systemUserDAO, times(1)).save(user);
    }

    @Test
    void optionalRoleWithoutTwoFactorShouldBypassRequirement() {
        SystemUser user = buildUser(Set.of(SystemActor.DOCTOR), false);

        TwoFactorResult result = service.processTwoFactor(user, null);

        assertFalse(result.isRequiresTwoFactor());
        assertTrue(result.isSuccessful());
        verifyNoInteractions(systemUserDAO);
    }

    @Test
    void lockoutShouldTriggerAfterConfiguredAttempts() {
        SystemUser user = buildUser(Set.of(SystemActor.PLANNER), true);

        TwoFactorResult firstFailure = service.processTwoFactor(user, "000000");
        TwoFactorResult lockout = service.processTwoFactor(user, "000000");

        assertEquals(HttpStatus.UNAUTHORIZED, firstFailure.getStatus());
        assertEquals(HttpStatus.TOO_MANY_REQUESTS, lockout.getStatus());
        verify(systemUserDAO, times(2)).save(user);
    }

    @Test
    void lockoutShouldReleaseAfterDuration() {
        SystemUser user = buildUser(Set.of(SystemActor.PLANNER), true);

        service.processTwoFactor(user, "badcode");
        TwoFactorResult lockout = service.processTwoFactor(user, "badcode");
        assertEquals(HttpStatus.TOO_MANY_REQUESTS, lockout.getStatus());

        mutableClock.advance(Duration.ofSeconds(61));
        String otp = codeService.currentCodeForUser(user);
        TwoFactorResult success = service.processTwoFactor(user, otp);

        assertTrue(success.isSuccessful());
        assertEquals(0, user.getOtpFailedAttempts());
    }

    @Test
    void enrollmentRequiredForEnforcedRolesWithoutOptIn() {
        SystemUser user = buildUser(Set.of(SystemActor.PLANNER), false);

        TwoFactorResult result = service.processTwoFactor(user, null);

        assertTrue(result.isRequiresTwoFactor());
        assertEquals(HttpStatus.FORBIDDEN, result.getStatus());
    }

    @Test
    void recoveryCodeAllowsJumpAheadAndRejectsStale() {
        SystemUser user = buildUser(Set.of(SystemActor.PLANNER), true);
        user.setLastRecoveryCodeIdUsed(1);

        String staleCode = codeService.recoveryCodeForUser(user, 1);
        TwoFactorResult staleAttempt = service.processTwoFactor(user, staleCode);
        assertEquals(HttpStatus.UNAUTHORIZED, staleAttempt.getStatus());

        String jumpAheadCode = codeService.recoveryCodeForUser(user, 3);
        TwoFactorResult jumpAheadResult = service.processTwoFactor(user, jumpAheadCode);

        assertTrue(jumpAheadResult.isSuccessful());
        assertEquals(3, user.getLastRecoveryCodeIdUsed());
    }

    @Test
    void finalRecoveryCodeDisablesTwoFactorAndRotatesSalt() {
        SystemUser user = buildUser(Set.of(SystemActor.PLANNER), true);
        String originalSalt = user.getTwoFaVersionOrSalt();
        user.setLastRecoveryCodeIdUsed(3);

        String finalCode = codeService.recoveryCodeForUser(user, properties.getRecoveryCodeCount());
        TwoFactorResult result = service.processTwoFactor(user, finalCode);

        assertTrue(result.isSuccessful());
        assertFalse(user.isTwoFactorEnabled());
        assertNotEquals(originalSalt, user.getTwoFaVersionOrSalt());
        assertEquals(0, user.getLastRecoveryCodeIdUsed());
    }

    private SystemUser buildUser(Set<SystemActor> roles, boolean twoFactorEnabled) {
        SystemUser user = new SystemUser(
                "John",
                "Doe",
                "TAXCODE",
                java.time.LocalDate.of(1990, 1, 1),
                "user@example.com",
                "password",
                roles,
                "public"
        );
        user.setTwoFactorEnabled(twoFactorEnabled);
        user.setTwoFaVersionOrSalt("v1");
        return user;
    }

    private static class MutableClock extends Clock {
        private Instant instant;

        MutableClock(Instant instant) {
            this.instant = instant;
        }

        void advance(Duration duration) {
            instant = instant.plus(duration);
        }

        @Override
        public ZoneId getZone() {
            return ZoneId.of("UTC");
        }

        @Override
        public Clock withZone(ZoneId zone) {
            return this;
        }

        @Override
        public Instant instant() {
            return instant;
        }
    }
}
