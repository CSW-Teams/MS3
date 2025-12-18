package org.cswteams.ms3.security;

import lombok.Data;
import org.cswteams.ms3.enums.SystemActor;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Data
@ConfigurationProperties(prefix = "security.2fa")
public class TwoFactorProperties {
    /**
     * Roles that must complete two-factor authentication before JWT issuance.
     */
    private List<SystemActor> requiredRoles = new ArrayList<>();

    /**
     * Master key used to derive deterministic TOTP secrets. Should be configured securely per environment.
     */
    private String masterKey = "change-me";

    /**
     * Number of digits generated/accepted for the TOTP code.
     */
    private int otpDigits = 6;

    /**
     * Allowed time-window drift (in 30s steps) for OTP validation.
     */
    private int allowedDriftWindows = 1;

    /**
     * Number of consecutive OTP failures before triggering a lockout.
     */
    private int lockoutThreshold = 5;

    /**
     * Duration of OTP lockout after reaching the threshold.
     */
    private Duration lockoutDuration = Duration.ofSeconds(60);
}
