package org.cswteams.ms3.rest;

import org.cswteams.ms3.dao.SystemUserDAO;
import org.cswteams.ms3.dto.login.CustomUserDetails;
import org.cswteams.ms3.entity.SystemUser;
import org.cswteams.ms3.security.TwoFactorAuthenticationService;
import org.cswteams.ms3.security.TwoFactorCodeService;
import org.cswteams.ms3.security.TwoFactorProperties;
import org.cswteams.ms3.security.TwoFactorVerificationOutcome;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.constraints.NotBlank;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/2fa")
public class TwoFactorRestEndpoint {

    private final TwoFactorCodeService codeService;
    private final TwoFactorAuthenticationService authenticationService;
    private final SystemUserDAO systemUserDAO;
    private final TwoFactorProperties properties;
    private final Clock clock;

    public TwoFactorRestEndpoint(TwoFactorCodeService codeService,
                                 TwoFactorAuthenticationService authenticationService,
                                 SystemUserDAO systemUserDAO,
                                 TwoFactorProperties properties,
                                 Clock clock) {
        this.codeService = codeService;
        this.authenticationService = authenticationService;
        this.systemUserDAO = systemUserDAO;
        this.properties = properties;
        this.clock = clock;
    }

    @GetMapping("/status")
    public ResponseEntity<TwoFactorStatusResponse> getStatus() {
        SystemUser user = getCurrentUser();
        boolean enabled = user.isTwoFactorEnabled();
        boolean enforcedByRole = isEnforcedForUser(user.getSystemActors());
        boolean enrollmentRequired = enforcedByRole && !enabled;

        String message = enabled
                ? "Two-factor authentication is enabled."
                : "Two-factor authentication is not enabled.";

        TwoFactorStatusResponse response = new TwoFactorStatusResponse(enabled, enrollmentRequired, message);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/enroll")
    public ResponseEntity<TwoFactorEnrollmentResponse> enroll() {
        SystemUser user = getCurrentUser();

        initializeEnrollmentState(user);

        try {
            byte[] secretBytes = codeService.deriveTotpSecret(user);
            String manualKey = base32Encode(secretBytes);
            String otpauthUrl = buildOtpAuthUrl(user.getEmail(), manualKey);
            List<String> recoveryCodes = buildRecoveryCodes(user);

            TwoFactorEnrollmentResponse response = new TwoFactorEnrollmentResponse(
                    "Two-factor enrollment started.",
                    null,
                    manualKey,
                    otpauthUrl,
                    recoveryCodes
            );
            return ResponseEntity.ok(response);
        } catch (GeneralSecurityException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Unable to prepare enrollment secrets.", e);
        }
    }

    @PostMapping("/confirm")
    public ResponseEntity<TwoFactorStatusResponse> confirm(@RequestBody TwoFactorCodeRequest request) {
        if (request == null || request.getCode() == null || request.getCode().isBlank()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new TwoFactorStatusResponse(false, false, "A code is required to confirm enrollment."));
        }

        SystemUser user = getCurrentUser();
        TwoFactorVerificationOutcome outcome = codeService.verify(user, request.getCode());
        if (!outcome.isValid()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new TwoFactorStatusResponse(false, true, "Invalid code. Please try again."));
        }

        authenticationService.markEnrollmentConfirmed(user);
        return ResponseEntity.ok(new TwoFactorStatusResponse(true, false, "Two-factor authentication enabled."));
    }

    @PostMapping("/disable")
    public ResponseEntity<TwoFactorStatusResponse> disable(@RequestBody TwoFactorDisableRequest request) {
        if (request == null || request.getCode() == null || request.getCode().isBlank()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new TwoFactorStatusResponse(true, false, "A code is required to disable two-factor authentication."));
        }

        SystemUser user = getCurrentUser();
        TwoFactorVerificationOutcome outcome = codeService.verify(user, request.getCode());
        if (!outcome.isValid()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new TwoFactorStatusResponse(user.isTwoFactorEnabled(), false, "Invalid code. Please try again."));
        }

        authenticationService.disableTwoFactor(user);
        user.setTwoFaVersionOrSalt(UUID.randomUUID().toString());
        user.setLastRecoveryCodeIdUsed(0);
        user.setEnrollmentConfirmedAt(null);
        systemUserDAO.save(user);

        String message = outcome.isFinalRecoveryCode()
                ? "Final recovery code used. Two-factor authentication disabled and secrets rotated."
                : "Two-factor authentication disabled.";
        return ResponseEntity.ok(new TwoFactorStatusResponse(false, false, message));
    }

    private void initializeEnrollmentState(SystemUser user) {
        user.setTwoFactorEnabled(false);
        user.setEnrollmentConfirmedAt(null);
        user.setLastRecoveryCodeIdUsed(0);
        user.setOtpFailedAttempts(0);
        user.setOtpLockoutEndsAt(null);
        user.setOtpLastAttemptAt(LocalDateTime.now(clock));
        if (user.getTwoFaVersionOrSalt() == null || user.getTwoFaVersionOrSalt().isBlank()) {
            user.setTwoFaVersionOrSalt(UUID.randomUUID().toString());
        }
        systemUserDAO.save(user);
    }

    private List<String> buildRecoveryCodes(SystemUser user) {
        int count = properties.getRecoveryCodeCount();
        if (count <= 0) {
            return Collections.emptyList();
        }
        List<String> codes = new ArrayList<>();
        for (int i = 1; i <= count; i++) {
            codes.add(codeService.recoveryCodeForUser(user, i));
        }
        return codes;
    }

    private boolean isEnforcedForUser(Set<?> roles) {
        if (roles == null || roles.isEmpty()) {
            return false;
        }
        return roles.stream().anyMatch(properties.getRequiredRoles()::contains);
    }

    private SystemUser getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getPrincipal() == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User is not authenticated");
        }

        Object principal = authentication.getPrincipal();
        SystemUser user = null;

        if (principal instanceof CustomUserDetails) {
            CustomUserDetails details = (CustomUserDetails) principal;
            user = systemUserDAO.findById(details.getId());
            if (user == null) {
                user = systemUserDAO.findByEmail(details.getEmail());
            }
        } else if (principal instanceof String) {
            user = systemUserDAO.findByEmail((String) principal);
        }

        if (user == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Authenticated user not found");
        }
        return user;
    }

    private String buildOtpAuthUrl(String email, String secret) {
        String label = URLEncoder.encode("MS3:" + email, StandardCharsets.UTF_8);
        String issuer = URLEncoder.encode("MS3", StandardCharsets.UTF_8);
        return String.format("otpauth://totp/%s?secret=%s&issuer=%s", label, secret, issuer);
    }

    private String base32Encode(byte[] data) {
        if (data == null || data.length == 0) {
            return "";
        }

        final char[] alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ234567".toCharArray();
        StringBuilder result = new StringBuilder((data.length * 8 + 4) / 5);
        int index = 0;
        while (index < data.length) {
            int currentByte = data[index++] & 0xff;
            result.append(alphabet[currentByte >> 3]);
            int digit = (currentByte & 0x07) << 2;

            if (index >= data.length) {
                result.append(alphabet[digit]);
                break;
            }

            currentByte = data[index++] & 0xff;
            result.append(alphabet[digit | (currentByte >> 6)]);
            result.append(alphabet[(currentByte >> 1) & 0x1f]);
            digit = (currentByte & 0x01) << 4;

            if (index >= data.length) {
                result.append(alphabet[digit]);
                break;
            }

            currentByte = data[index++] & 0xff;
            result.append(alphabet[digit | (currentByte >> 4)]);
            digit = (currentByte & 0x0f) << 1;

            if (index >= data.length) {
                result.append(alphabet[digit]);
                break;
            }

            currentByte = data[index++] & 0xff;
            result.append(alphabet[digit | (currentByte >> 7)]);
            result.append(alphabet[(currentByte >> 2) & 0x1f]);
            digit = (currentByte & 0x03) << 3;

            if (index >= data.length) {
                result.append(alphabet[digit]);
                break;
            }

            currentByte = data[index++] & 0xff;
            result.append(alphabet[digit | (currentByte >> 5)]);
            result.append(alphabet[currentByte & 0x1f]);
        }
        return result.toString();
    }

    private static class TwoFactorStatusResponse {
        private final boolean enabled;
        private final boolean enrollmentRequired;
        private final String message;

        public TwoFactorStatusResponse(boolean enabled, boolean enrollmentRequired, String message) {
            this.enabled = enabled;
            this.enrollmentRequired = enrollmentRequired;
            this.message = message;
        }

        public boolean isEnabled() {
            return enabled;
        }

        public boolean isEnrollmentRequired() {
            return enrollmentRequired;
        }

        public String getMessage() {
            return message;
        }
    }

    private static class TwoFactorEnrollmentResponse extends TwoFactorStatusResponse {
        private final String qrImage;
        private final String manualKey;
        private final String otpauthUrl;
        private final List<String> recoveryCodes;

        public TwoFactorEnrollmentResponse(String message, String qrImage, String manualKey, String otpauthUrl, List<String> recoveryCodes) {
            super(false, true, message);
            this.qrImage = qrImage;
            this.manualKey = manualKey;
            this.otpauthUrl = otpauthUrl;
            this.recoveryCodes = recoveryCodes;
        }

        public String getQrImage() {
            return qrImage;
        }

        public String getManualKey() {
            return manualKey;
        }

        public String getOtpauthUrl() {
            return otpauthUrl;
        }

        public List<String> getRecoveryCodes() {
            return recoveryCodes;
        }
    }

    private static class TwoFactorCodeRequest {
        @NotBlank
        private String code;

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }
    }

    private static class TwoFactorDisableRequest extends TwoFactorCodeRequest {
        private boolean isRecoveryCode;

        public boolean isRecoveryCode() {
            return isRecoveryCode;
        }

        public void setRecoveryCode(boolean recoveryCode) {
            isRecoveryCode = recoveryCode;
        }
    }
}
