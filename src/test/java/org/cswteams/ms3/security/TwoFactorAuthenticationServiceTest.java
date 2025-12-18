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
import java.time.Instant;
import java.time.ZoneOffset;
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
    private Clock fixedClock;

    @BeforeEach
    void setUp() {
        properties = new TwoFactorProperties();
        properties.setRequiredRoles(List.of(SystemActor.PLANNER));
        properties.setMasterKey("unit-test-master-key");
        properties.setAllowedDriftWindows(0);
        properties.setLockoutThreshold(2);

        fixedClock = Clock.fixed(Instant.parse("2024-01-01T00:00:00Z"), ZoneOffset.UTC);
        codeService = new TwoFactorCodeService(properties, fixedClock);
        service = new TwoFactorAuthenticationService(properties, codeService, systemUserDAO, fixedClock);
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
    void enrollmentRequiredForEnforcedRolesWithoutOptIn() {
        SystemUser user = buildUser(Set.of(SystemActor.PLANNER), false);

        TwoFactorResult result = service.processTwoFactor(user, null);

        assertTrue(result.isRequiresTwoFactor());
        assertEquals(HttpStatus.FORBIDDEN, result.getStatus());
    }

    private SystemUser buildUser(Set<SystemActor> roles, boolean twoFactorEnabled) {
        SystemUser user = new SystemUser();
        user.setEmail("user@example.com");
        user.setSystemActors(roles);
        user.setTwoFactorEnabled(twoFactorEnabled);
        user.setTwoFaVersionOrSalt("v1");
        return user;
    }
}
