-- Purpose: backfill 2FA lifecycle columns required by secure login and account recovery flows.
-- Context: executed by SchemasInitializer with current search_path (public first, then each tenant schema).
-- Order: run after base user tables exist; safe to re-run thanks to IF NOT EXISTS guards.

-- Stores OTP secret version/salt so secret rotations invalidate stale OTP material.
ALTER TABLE IF EXISTS ms3_system_user
    ADD COLUMN IF NOT EXISTS two_fa_version_or_salt VARCHAR(255);

-- Toggles OTP challenge enforcement only after user opts in.
ALTER TABLE IF EXISTS ms3_system_user
    ADD COLUMN IF NOT EXISTS two_factor_enabled BOOLEAN NOT NULL DEFAULT false;

-- Marks when enrollment completed to distinguish setup from active 2FA usage.
ALTER TABLE IF EXISTS ms3_system_user
    ADD COLUMN IF NOT EXISTS enrollment_confirmed_at TIMESTAMP;

-- Remembers latest consumed recovery code to prevent replay.
ALTER TABLE IF EXISTS ms3_system_user
    ADD COLUMN IF NOT EXISTS last_recovery_code_id_used INTEGER NOT NULL DEFAULT 0;

-- Counts failed OTP attempts for brute-force protection.
ALTER TABLE IF EXISTS ms3_system_user
    ADD COLUMN IF NOT EXISTS otp_failed_attempts INTEGER NOT NULL DEFAULT 0;

-- Defines lockout expiry after repeated OTP failures.
ALTER TABLE IF EXISTS ms3_system_user
    ADD COLUMN IF NOT EXISTS otp_lockout_ends_at TIMESTAMP;

-- Captures last OTP attempt time for lockout and audit checks.
ALTER TABLE IF EXISTS ms3_system_user
    ADD COLUMN IF NOT EXISTS otp_last_attempt_at TIMESTAMP;

-- Tenant tables mirror the same 2FA state; script runs once per schema via search_path.
ALTER TABLE IF EXISTS ms3_tenant_user
    ADD COLUMN IF NOT EXISTS two_fa_version_or_salt VARCHAR(255);

ALTER TABLE IF EXISTS ms3_tenant_user
    ADD COLUMN IF NOT EXISTS two_factor_enabled BOOLEAN NOT NULL DEFAULT false;

ALTER TABLE IF EXISTS ms3_tenant_user
    ADD COLUMN IF NOT EXISTS enrollment_confirmed_at TIMESTAMP;

ALTER TABLE IF EXISTS ms3_tenant_user
    ADD COLUMN IF NOT EXISTS last_recovery_code_id_used INTEGER NOT NULL DEFAULT 0;

ALTER TABLE IF EXISTS ms3_tenant_user
    ADD COLUMN IF NOT EXISTS otp_failed_attempts INTEGER NOT NULL DEFAULT 0;

ALTER TABLE IF EXISTS ms3_tenant_user
    ADD COLUMN IF NOT EXISTS otp_lockout_ends_at TIMESTAMP;

ALTER TABLE IF EXISTS ms3_tenant_user
    ADD COLUMN IF NOT EXISTS otp_last_attempt_at TIMESTAMP;
