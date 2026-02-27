package org.cswteams.ms3.security;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
/**
 * Represents the 2FA decision returned by the authentication service and consumed by login handlers.
 *
 * <p>Business rule: each factory method maps a security outcome to the HTTP status used by REST endpoints.</p>
 */
public class TwoFactorResult {
    private final boolean requiresTwoFactor;
    private final boolean successful;
    private final HttpStatus status;
    private final String message;

    private TwoFactorResult(boolean requiresTwoFactor, boolean successful, HttpStatus status, String message) {
        this.requiresTwoFactor = requiresTwoFactor;
        this.successful = successful;
        this.status = status;
        this.message = message;
    }

    public static TwoFactorResult notRequired() {
        return new TwoFactorResult(false, true, HttpStatus.OK, null);
    }

    public static TwoFactorResult enrollmentRequired(String message) {
        return new TwoFactorResult(true, false, HttpStatus.FORBIDDEN, message);
    }

    public static TwoFactorResult challenge(String message) {
        return new TwoFactorResult(true, false, HttpStatus.FORBIDDEN, message);
    }

    public static TwoFactorResult locked(String message) {
        return new TwoFactorResult(true, false, HttpStatus.TOO_MANY_REQUESTS, message);
    }

    public static TwoFactorResult failed(String message) {
        return new TwoFactorResult(true, false, HttpStatus.UNAUTHORIZED, message);
    }

    public static TwoFactorResult successWithTwoFactor() {
        return new TwoFactorResult(true, true, HttpStatus.OK, null);
    }
}
