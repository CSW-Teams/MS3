DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_roles WHERE rolname = 'user_ms3_public') THEN
        CREATE USER user_ms3_public WITH PASSWORD 'password_public';
    END IF;

    IF NOT EXISTS (SELECT 1 FROM pg_roles WHERE rolname = 'user_ms3_a') THEN
        CREATE USER user_ms3_a WITH PASSWORD 'password_a';
    END IF;

    IF NOT EXISTS (SELECT 1 FROM pg_roles WHERE rolname = 'user_ms3_b') THEN
        CREATE USER user_ms3_b WITH PASSWORD 'password_b';
    END IF;
END
$$;
