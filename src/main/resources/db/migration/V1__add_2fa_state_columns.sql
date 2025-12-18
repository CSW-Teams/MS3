ALTER TABLE IF EXISTS ms3_system_user
    ADD COLUMN IF NOT EXISTS two_fa_version_or_salt VARCHAR(255);

ALTER TABLE IF EXISTS ms3_system_user
    ADD COLUMN IF NOT EXISTS two_factor_enabled BOOLEAN NOT NULL DEFAULT false;

ALTER TABLE IF EXISTS ms3_system_user
    ADD COLUMN IF NOT EXISTS enrollment_confirmed_at TIMESTAMP;

ALTER TABLE IF EXISTS ms3_system_user
    ADD COLUMN IF NOT EXISTS last_recovery_code_id_used INTEGER NOT NULL DEFAULT 0;

ALTER TABLE IF EXISTS ms3_system_user
    ADD COLUMN IF NOT EXISTS otp_failed_attempts INTEGER NOT NULL DEFAULT 0;

ALTER TABLE IF EXISTS ms3_system_user
    ADD COLUMN IF NOT EXISTS otp_lockout_ends_at TIMESTAMP;

ALTER TABLE IF EXISTS ms3_system_user
    ADD COLUMN IF NOT EXISTS otp_last_attempt_at TIMESTAMP;
