package org.cswteams.ms3.security;

import org.cswteams.ms3.entity.SystemUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.time.Clock;
import java.time.Instant;
import java.util.Base64;

@Component
public class TwoFactorCodeService {

    private static final Logger logger = LoggerFactory.getLogger(TwoFactorCodeService.class);
    private static final String HMAC_SHA_1 = "HmacSHA1";
    private static final String HMAC_SHA_256 = "HmacSHA256";

    private final TwoFactorProperties properties;
    private final Clock clock;

    public TwoFactorCodeService(TwoFactorProperties properties, Clock clock) {
        this.properties = properties;
        this.clock = clock;
    }

    public TwoFactorVerificationOutcome verify(SystemUser user, String providedCode) {
        if (providedCode == null) {
            return TwoFactorVerificationOutcome.invalid();
        }
        String sanitizedCode = providedCode.trim();
        if (verifyTotp(user, sanitizedCode)) {
            return TwoFactorVerificationOutcome.successTotp();
        }

        RecoveryVerificationResult recoveryVerificationResult = verifyRecoveryCode(user, sanitizedCode);
        if (recoveryVerificationResult.isValid()) {
            user.setLastRecoveryCodeIdUsed(recoveryVerificationResult.getCodeId());
            return TwoFactorVerificationOutcome.successRecovery(recoveryVerificationResult.isFinalCode());
        }

        logger.warn("2FA code verification failed for user {}", user.getEmail());
        return TwoFactorVerificationOutcome.invalid();
    }

    public String currentCodeForUser(SystemUser user) {
        long currentWindow = Instant.now(clock).getEpochSecond() / 30L;
        return generateTotp(user, currentWindow);
    }

    public String recoveryCodeForUser(SystemUser user, int codeId) {
        try {
            byte[] secret = deriveSecret(user, "recovery:" + codeId);
            String code = Base64.getUrlEncoder().withoutPadding().encodeToString(secret);
            return code.length() <= properties.getRecoveryCodeLength()
                    ? code
                    : code.substring(0, properties.getRecoveryCodeLength());
        } catch (GeneralSecurityException e) {
            logger.error("Failed to generate recovery code", e);
            return "";
        }
    }

    private boolean verifyTotp(SystemUser user, String sanitizedCode) {
        long currentWindow = Instant.now(clock).getEpochSecond() / 30L;
        for (int offset = -properties.getAllowedDriftWindows(); offset <= properties.getAllowedDriftWindows(); offset++) {
            String expected = generateTotp(user, currentWindow + offset);
            if (expected.equals(sanitizedCode)) {
                return true;
            }
        }
        return false;
    }

    private RecoveryVerificationResult verifyRecoveryCode(SystemUser user, String sanitizedCode) {
        int nextExpectedId = user.getLastRecoveryCodeIdUsed() + 1;
        for (int i = nextExpectedId; i <= properties.getRecoveryCodeCount(); i++) {
            if (recoveryCodeForUser(user, i).equals(sanitizedCode)) {
                boolean finalCode = i == properties.getRecoveryCodeCount();
                return RecoveryVerificationResult.success(i, finalCode);
            }
        }
        return RecoveryVerificationResult.invalid();
    }

    private String generateTotp(SystemUser user, long timeWindow) {
        try {
            byte[] secret = deriveSecret(user, "totp");
            Mac mac = Mac.getInstance(HMAC_SHA_1);
            mac.init(new SecretKeySpec(secret, HMAC_SHA_1));
            byte[] data = ByteBuffer.allocate(8).putLong(timeWindow).array();
            byte[] hash = mac.doFinal(data);

            int offset = hash[hash.length - 1] & 0xF;
            int binary = ((hash[offset] & 0x7f) << 24)
                    | ((hash[offset + 1] & 0xff) << 16)
                    | ((hash[offset + 2] & 0xff) << 8)
                    | (hash[offset + 3] & 0xff);
            int otp = binary % (int) Math.pow(10, properties.getOtpDigits());
            return String.format("%0" + properties.getOtpDigits() + "d", otp);
        } catch (GeneralSecurityException e) {
            logger.error("Failed to generate TOTP code", e);
            return "";
        }
    }

    private byte[] deriveSecret(SystemUser user, String purpose) throws GeneralSecurityException {
        Mac mac = Mac.getInstance(HMAC_SHA_256);
        mac.init(new SecretKeySpec(properties.getMasterKey().getBytes(StandardCharsets.UTF_8), HMAC_SHA_256));
        String salt = user.getTwoFaVersionOrSalt() == null ? "" : user.getTwoFaVersionOrSalt();
        String payload = user.getEmail() + ":" + salt + ":" + purpose;
        return mac.doFinal(payload.getBytes(StandardCharsets.UTF_8));
    }

    private static class RecoveryVerificationResult {
        private final boolean valid;
        private final int codeId;
        private final boolean finalCode;

        private RecoveryVerificationResult(boolean valid, int codeId, boolean finalCode) {
            this.valid = valid;
            this.codeId = codeId;
            this.finalCode = finalCode;
        }

        public static RecoveryVerificationResult success(int codeId, boolean finalCode) {
            return new RecoveryVerificationResult(true, codeId, finalCode);
        }

        public static RecoveryVerificationResult invalid() {
            return new RecoveryVerificationResult(false, -1, false);
        }

        public boolean isValid() {
            return valid;
        }

        public int getCodeId() {
            return codeId;
        }

        public boolean isFinalCode() {
            return finalCode;
        }
    }
}
