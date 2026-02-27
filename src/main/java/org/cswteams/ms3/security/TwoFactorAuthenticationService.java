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
import java.util.UUID;

@Service
/**
 * Applies 2FA business rules during login: enrollment, challenge, lockout, and success paths.
 */
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

    /**
     * Evaluates whether 2FA is required and validates the provided code when needed.
     */
    public TwoFactorResult processTwoFactor(SystemUser user, String providedCode) {
        boolean enforcedByRole = isEnforcedForUser(user);
        boolean enabled = user.isTwoFactorEnabled();

        // Business rule: if role policy does not enforce 2FA and user never enabled it, login proceeds with password only.
        if (!enforcedByRole && !enabled) {
            return TwoFactorResult.notRequired();
        }

        // Business rule: role requires 2FA, but enrollment has not been completed yet.
        if (!enabled) {
            logger.info("2FA enrollment required for user {} due to enforced role", user.getEmail());
            return TwoFactorResult.enrollmentRequired("Two-factor enrollment is required before login.");
        }

        // Business rule: repeated failures trigger temporary lockout.
        if (isLockedOut(user)) {
            logger.warn("2FA lockout active for user {} until {}", user.getEmail(), user.getOtpLockoutEndsAt());
            return TwoFactorResult.locked("Too many failed attempts. Please wait before retrying.");
        }

        // Business rule: once 2FA is active, code is mandatory to finish login.
        if (providedCode == null || providedCode.isBlank()) {
            return TwoFactorResult.challenge("Two-factor code required.");
        }

        TwoFactorVerificationOutcome verificationOutcome = codeService.verify(user, providedCode);
        if (!verificationOutcome.isValid()) {
            registerFailure(user);
            return TwoFactorResult.failed("Invalid two-factor code.");
        }

        if (verificationOutcome.isFinalRecoveryCode()) {
            applyFinalRecoveryCode(user);
        } else {
            resetFailures(user);
        }

        logger.info("2FA verification succeeded for user {}", user.getEmail());
        return TwoFactorResult.successWithTwoFactor();
    }

    /**
     * Marks enrollment as completed after successful confirmation code submission.
     */
    public void markEnrollmentConfirmed(SystemUser user) {
        user.setTwoFactorEnabled(true);
        user.setEnrollmentConfirmedAt(LocalDateTime.now(clock));
        systemUserDAO.save(user);
        logger.info("2FA enrollment confirmed for user {}", user.getEmail());
    }

    /**
     * Disables 2FA and clears enrollment metadata for the authenticated user.
     */
    public void disableTwoFactor(SystemUser user) {
        user.setTwoFactorEnabled(false);
        user.setTwoFaVersionOrSalt(null);
        user.setEnrollmentConfirmedAt(null);
        resetFailures(user);
        systemUserDAO.save(user);
        logger.info("2FA disabled for user {}", user.getEmail());
    }

    /**
     * Tracks failed code attempts and starts lockout when the configured threshold is reached.
     */
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

    /**
     * Returns true while lockout expiration is still in the future.
     */
    private boolean isLockedOut(SystemUser user) {
        return user.getOtpLockoutEndsAt() != null && user.getOtpLockoutEndsAt().isAfter(LocalDateTime.now(clock));
    }

    /**
     * Returns true when at least one of the user roles is listed in required 2FA roles.
     */
    private boolean isEnforcedForUser(SystemUser user) {
        Set<SystemActor> roles = user.getSystemActors();
        if (roles == null || roles.isEmpty()) {
            return false;
        }
        return roles.stream().anyMatch(properties.getRequiredRoles()::contains);
    }

    /**
     * Applies the final recovery-code rule: disable 2FA and force full re-enrollment.
     */
    private void applyFinalRecoveryCode(SystemUser user) {
        // Final recovery code intentionally invalidates current enrollment so a stolen backup-code
        // list cannot be reused indefinitely without a fresh secret provisioning cycle.
        user.setTwoFactorEnabled(false);
        user.setTwoFaVersionOrSalt(UUID.randomUUID().toString());
        user.setEnrollmentConfirmedAt(null);
        user.setLastRecoveryCodeIdUsed(0);
        resetFailures(user);
    }
}
