-- Rimuovi il database
    DROP DATABASE IF EXISTS ms3_a;
    DROP DATABASE IF EXISTS ms3_b;
    DROP DATABASE IF EXISTS ms3_public;

-- Crea il database
    CREATE DATABASE ms3_a;
    CREATE DATABASE ms3_b;
    CREATE DATABASE ms3_public;

DO $$
BEGIN

    -- Cancellare lo schema 'public' nel database ms3_a
    PERFORM dblink_exec(
        'host=localhost dbname=ms3_a user=sprintfloyd password=sprintfloyd',
        'DROP SCHEMA IF EXISTS public CASCADE'
    );

    PERFORM dblink_exec(
        'host=localhost dbname=ms3_a user=sprintfloyd password=sprintfloyd',
        'CREATE SCHEMA public'
    );

    -- Cancellare lo schema 'public' nel database ms3_b
    PERFORM dblink_exec(
        'host=localhost dbname=ms3_b user=sprintfloyd password=sprintfloyd',
        'DROP SCHEMA IF EXISTS public CASCADE'
    );

    PERFORM dblink_exec(
        'host=localhost dbname=ms3_b user=sprintfloyd password=sprintfloyd',
        'CREATE SCHEMA public'
    );

    PERFORM dblink_exec(
            'host=localhost dbname=ms3_public user=sprintfloyd password=sprintfloyd',
            'DROP SCHEMA IF EXISTS public CASCADE'
        );

        PERFORM dblink_exec(
            'host=localhost dbname=ms3_public user=sprintfloyd password=sprintfloyd',
            'CREATE SCHEMA public'
        );
END $$;