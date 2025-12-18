package org.cswteams.ms3.security;

import org.cswteams.ms3.dao.SystemUserDAO;
import org.cswteams.ms3.entity.SystemUser;
import org.cswteams.ms3.enums.SystemActor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Set;

@Service
public class TwoFactorAuthenticationService {
    private static final Logger logger = LoggerFactory.getLogger(TwoFactorAuthenticationService.class);

    private final TwoFactorProperties properties;
    private final TwoFactorCodeService codeService;
    private final SystemUserDAO systemUserDAO;
    private final Clock clock;

    public TwoFactorAuthenticationService(TwoFactorProperties properties,
                                          TwoFactorCodeService codeService,
                                          SystemUserDAO systemUserDAO,
                                          Clock clock) {
        this.properties = properties;
        this.codeService = codeService;
        this.systemUserDAO = systemUserDAO;
        this.clock = clock;
    }

    public TwoFactorResult processTwoFactor(SystemUser user, String providedCode) {
        boolean enforcedByRole = isEnforcedForUser(user);
        boolean enabled = user.isTwoFactorEnabled();

        if (!enforcedByRole && !enabled) {
            return TwoFactorResult.notRequired();
        }

        if (!enabled) {
            logger.info("2FA enrollment required for user {} due to enforced role", user.getEmail());
            return TwoFactorResult.enrollmentRequired("Two-factor enrollment is required before login.");
        }

        if (isLockedOut(user)) {
            logger.warn("2FA lockout active for user {} until {}", user.getEmail(), user.getOtpLockoutEndsAt());
            return TwoFactorResult.locked("Too many failed attempts. Please wait before retrying.");
        }

        if (providedCode == null || providedCode.isBlank()) {
            return TwoFactorResult.challenge("Two-factor code required.");
        }

        boolean valid = codeService.verifyCode(user, providedCode);
        if (!valid) {
            registerFailure(user);
            return TwoFactorResult.failed("Invalid two-factor code.");
        }

        resetFailures(user);
        logger.info("2FA verification succeeded for user {}", user.getEmail());
        return TwoFactorResult.successWithTwoFactor();
    }

    public void markEnrollmentConfirmed(SystemUser user) {
        user.setTwoFactorEnabled(true);
        user.setEnrollmentConfirmedAt(LocalDateTime.now(clock));
        systemUserDAO.save(user);
        logger.info("2FA enrollment confirmed for user {}", user.getEmail());
    }

    public void disableTwoFactor(SystemUser user) {
        user.setTwoFactorEnabled(false);
        user.setTwoFaVersionOrSalt(null);
        user.setEnrollmentConfirmedAt(null);
        resetFailures(user);
        systemUserDAO.save(user);
        logger.info("2FA disabled for user {}", user.getEmail());
    }

    private void registerFailure(SystemUser user) {
        user.setOtpFailedAttempts(user.getOtpFailedAttempts() + 1);
        user.setOtpLastAttemptAt(LocalDateTime.now(clock));
        if (user.getOtpFailedAttempts() >= properties.getLockoutThreshold()) {
            user.setOtpLockoutEndsAt(LocalDateTime.now(clock).plus(properties.getLockoutDuration()));
            logger.warn("2FA lockout triggered for user {} after {} failures", user.getEmail(), user.getOtpFailedAttempts());
        }
        systemUserDAO.save(user);
    }

    private void resetFailures(SystemUser user) {
        user.setOtpFailedAttempts(0);
        user.setOtpLockoutEndsAt(null);
        user.setOtpLastAttemptAt(null);
        systemUserDAO.save(user);
    }

    private boolean isLockedOut(SystemUser user) {
        return user.getOtpLockoutEndsAt() != null && user.getOtpLockoutEndsAt().isAfter(LocalDateTime.now(clock));
    }

    private boolean isEnforcedForUser(SystemUser user) {
        Set<SystemActor> roles = user.getSystemActors();
        if (roles == null || roles.isEmpty()) {
            return false;
        }
        return roles.stream().anyMatch(properties.getRequiredRoles()::contains);
    }
}
