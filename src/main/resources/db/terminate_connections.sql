DO $$
DECLARE
    db_name TEXT;
    session_id TEXT;
BEGIN
    FOR db_name IN
        SELECT unnest(ARRAY['ms3_a', 'ms3_b', 'ms3_public'])
    LOOP
        -- Termina tutte le connessioni al database
        FOR session_id IN
            SELECT pg_stat_activity.pid
            FROM pg_stat_activity
            WHERE pg_stat_activity.datname = db_name
              AND pg_stat_activity.pid <> pg_backend_pid()
        LOOP
            EXECUTE format('SELECT pg_terminate_backend(%s)', session_id);
            RAISE NOTICE 'Terminating session % for database %', session_id, db_name;
        END LOOP;
    END LOOP;
END $$;
