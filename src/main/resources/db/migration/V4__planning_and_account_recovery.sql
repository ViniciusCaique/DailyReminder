ALTER TABLE tb_activity ADD COLUMN completed boolean NOT NULL DEFAULT false;
ALTER TABLE tb_activity ADD COLUMN completed_at timestamp(6) with time zone;
ALTER TABLE tb_notes ADD COLUMN recurrence varchar(20) NOT NULL DEFAULT 'NONE';
ALTER TABLE tb_notes ADD COLUMN time_zone varchar(80) NOT NULL DEFAULT 'UTC';
ALTER TABLE tb_notes ADD COLUMN repeat_days varchar(20) NOT NULL DEFAULT '';
ALTER TABLE tb_notes ADD COLUMN last_completed_at timestamp(6) with time zone;
-- Preserve access for accounts that existed before verification was introduced.
ALTER TABLE tb_users ADD COLUMN email_verified boolean NOT NULL DEFAULT true;
ALTER TABLE tb_users ALTER COLUMN email_verified SET DEFAULT false;
ALTER TABLE tb_users ADD COLUMN credential_version integer NOT NULL DEFAULT 0;
CREATE UNIQUE INDEX ux_tb_users_email ON tb_users(lower(email));
CREATE TABLE tb_account_tokens (
    id uuid PRIMARY KEY,
    user_id uuid NOT NULL REFERENCES tb_users(id),
    token_hash varchar(64) NOT NULL UNIQUE,
    purpose varchar(30) NOT NULL,
    expires_at timestamp(6) with time zone NOT NULL,
    used boolean NOT NULL DEFAULT false
);
CREATE INDEX ix_account_tokens_user ON tb_account_tokens(user_id);
