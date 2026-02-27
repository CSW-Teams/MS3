ALTER TABLE schedule_feedback
    ALTER COLUMN category TYPE varchar(255)
    USING category::text;

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
    ALTER COLUMN category SET NOT NULL;

ALTER TABLE schedule_feedback
    DROP CONSTRAINT IF EXISTS ck_schedule_feedback_category;

ALTER TABLE schedule_feedback
    ADD CONSTRAINT ck_schedule_feedback_category
    CHECK (category IN (
        'REPEATED_WEEKDAY',
        'REPEATED_TIME_SLOT',
        'CONSECUTIVE_SHIFTS',
        'WORKLOAD_IMBALANCE',
        'PREFERENCE_VIOLATION',
        'OTHER'
    ));

DROP TYPE IF EXISTS feedback_category_enum;
