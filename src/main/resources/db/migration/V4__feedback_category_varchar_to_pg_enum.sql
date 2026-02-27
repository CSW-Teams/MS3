-- Idempotent enum migration: Flyway can run this script in environments that were
-- partially migrated during previous releases, so type creation must tolerate re-execution.
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'feedback_category_enum') THEN
        CREATE TYPE feedback_category_enum AS ENUM (
            'REPEATED_WEEKDAY',
            'REPEATED_TIME_SLOT',
            'CONSECUTIVE_SHIFTS',
            'WORKLOAD_IMBALANCE',
            'PREFERENCE_VIOLATION',
            'OTHER'
        );
    END IF;
END
$$;

-- Normalize legacy/free-text values before casting, otherwise PostgreSQL aborts the type conversion.
UPDATE schedule_feedback
SET category = 'OTHER'
WHERE category IS NULL
   OR category NOT IN (
       'REPEATED_WEEKDAY',
       'REPEATED_TIME_SLOT',
       'CONSECUTIVE_SHIFTS',
       'WORKLOAD_IMBALANCE',
       'PREFERENCE_VIOLATION',
       'OTHER'
   );

ALTER TABLE schedule_feedback
    DROP CONSTRAINT IF EXISTS ck_schedule_feedback_category;

-- Safe cast after normalization keeps historical rows while enforcing the canonical taxonomy.
ALTER TABLE schedule_feedback
    ALTER COLUMN category TYPE feedback_category_enum
    USING category::feedback_category_enum;

ALTER TABLE schedule_feedback
    ALTER COLUMN category SET NOT NULL;
