CREATE TABLE IF NOT EXISTS tb_users (
    id uuid PRIMARY KEY,
    name varchar(255),
    email varchar(255),
    password varchar(255),
    created_at timestamp(6),
    updated_at timestamp(6)
);

CREATE TABLE IF NOT EXISTS tb_notes (
    id uuid PRIMARY KEY,
    message varchar(255),
    duration integer NOT NULL
);

CREATE TABLE IF NOT EXISTS tb_activity (
    id uuid PRIMARY KEY,
    duration integer NOT NULL,
    data_dia date,
    name varchar(255),
    created_at timestamp(6),
    lembrete_id uuid NOT NULL,
    user_id uuid NOT NULL
);

CREATE TABLE IF NOT EXISTS tb_user_tokens (
    id uuid PRIMARY KEY,
    user_id uuid NOT NULL,
    token_hash varchar(64) NOT NULL,
    is_valid boolean NOT NULL DEFAULT true,
    expires_at timestamp(6) with time zone NOT NULL,
    created_at timestamp(6),
    updated_at timestamp(6)
);

ALTER TABLE tb_activity ADD COLUMN IF NOT EXISTS lembrete_id uuid;
ALTER TABLE tb_activity ADD COLUMN IF NOT EXISTS user_id uuid;

ALTER TABLE tb_user_tokens ADD COLUMN IF NOT EXISTS user_id uuid;
ALTER TABLE tb_user_tokens ADD COLUMN IF NOT EXISTS token_hash varchar(64);
ALTER TABLE tb_user_tokens ADD COLUMN IF NOT EXISTS is_valid boolean NOT NULL DEFAULT true;
ALTER TABLE tb_user_tokens ADD COLUMN IF NOT EXISTS expires_at timestamp(6) with time zone;
ALTER TABLE tb_user_tokens ADD COLUMN IF NOT EXISTS created_at timestamp(6);
ALTER TABLE tb_user_tokens ADD COLUMN IF NOT EXISTS updated_at timestamp(6);

DO $$
BEGIN
    IF EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_name = 'tb_user_tokens'
          AND column_name = 'invalidated'
    ) THEN
        UPDATE tb_user_tokens
        SET is_valid = NOT invalidated
        WHERE invalidated IS NOT NULL;
    END IF;
END $$;

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM pg_constraint
        WHERE conname = 'uk_tb_user_tokens_token_hash'
    ) THEN
        ALTER TABLE tb_user_tokens
        ADD CONSTRAINT uk_tb_user_tokens_token_hash UNIQUE (token_hash);
    END IF;
END $$;

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM pg_constraint
        WHERE conname = 'fk_tb_activity_lembrete'
    ) THEN
        ALTER TABLE tb_activity
        ADD CONSTRAINT fk_tb_activity_lembrete FOREIGN KEY (lembrete_id) REFERENCES tb_notes (id);
    END IF;
END $$;

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM pg_constraint
        WHERE conname = 'fk_tb_activity_user'
    ) THEN
        ALTER TABLE tb_activity
        ADD CONSTRAINT fk_tb_activity_user FOREIGN KEY (user_id) REFERENCES tb_users (id);
    END IF;
END $$;

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM pg_constraint
        WHERE conname = 'fk_tb_user_tokens_user'
    ) THEN
        ALTER TABLE tb_user_tokens
        ADD CONSTRAINT fk_tb_user_tokens_user FOREIGN KEY (user_id) REFERENCES tb_users (id);
    END IF;
END $$;
