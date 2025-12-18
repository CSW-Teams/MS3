package org.cswteams.ms3.security;

import lombok.Getter;

@Getter
public class TwoFactorVerificationOutcome {
    private final boolean valid;
    private final boolean usedRecoveryCode;
    private final boolean finalRecoveryCode;

    private TwoFactorVerificationOutcome(boolean valid, boolean usedRecoveryCode, boolean finalRecoveryCode) {
        this.valid = valid;
        this.usedRecoveryCode = usedRecoveryCode;
        this.finalRecoveryCode = finalRecoveryCode;
    }

    public static TwoFactorVerificationOutcome invalid() {
        return new TwoFactorVerificationOutcome(false, false, false);
    }

    public static TwoFactorVerificationOutcome successTotp() {
        return new TwoFactorVerificationOutcome(true, false, false);
    }

    public static TwoFactorVerificationOutcome successRecovery(boolean finalRecoveryCode) {
        return new TwoFactorVerificationOutcome(true, true, finalRecoveryCode);
    }
}
