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

    public boolean verifyCode(SystemUser user, String providedCode) {
        if (providedCode == null) {
            return false;
        }
        String sanitizedCode = providedCode.trim();
        long currentWindow = Instant.now(clock).getEpochSecond() / 30L;
        for (int offset = -properties.getAllowedDriftWindows(); offset <= properties.getAllowedDriftWindows(); offset++) {
            String expected = generateCode(user, currentWindow + offset);
            if (expected.equals(sanitizedCode)) {
                return true;
            }
        }
        logger.warn("2FA code verification failed for user {}", user.getEmail());
        return false;
    }

    public String currentCodeForUser(SystemUser user) {
        long currentWindow = Instant.now(clock).getEpochSecond() / 30L;
        return generateCode(user, currentWindow);
    }

    private String generateCode(SystemUser user, long timeWindow) {
        try {
            byte[] secret = deriveSecret(user);
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

    private byte[] deriveSecret(SystemUser user) throws GeneralSecurityException {
        Mac mac = Mac.getInstance(HMAC_SHA_256);
        mac.init(new SecretKeySpec(properties.getMasterKey().getBytes(StandardCharsets.UTF_8), HMAC_SHA_256));
        String salt = user.getTwoFaVersionOrSalt() == null ? "" : user.getTwoFaVersionOrSalt();
        String payload = user.getEmail() + ":" + salt + ":totp";
        return mac.doFinal(payload.getBytes(StandardCharsets.UTF_8));
    }
}
