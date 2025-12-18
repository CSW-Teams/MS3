-- Shared blacklist table for revoked/banned JWT tokens
-- Idempotent definitions keep container startup stable across restarts
CREATE SEQUENCE IF NOT EXISTS public.blacklisted_tokens_id_seq;

CREATE TABLE IF NOT EXISTS public.blacklisted_tokens (
    id BIGINT NOT NULL DEFAULT nextval('public.blacklisted_tokens_id_seq'),
    token VARCHAR(1024) NOT NULL,
    blacklisted_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT now(),
    CONSTRAINT blacklisted_tokens_pkey PRIMARY KEY (id),
    CONSTRAINT blacklisted_tokens_token_key UNIQUE (token)
);

ALTER SEQUENCE public.blacklisted_tokens_id_seq OWNED BY public.blacklisted_tokens.id;
