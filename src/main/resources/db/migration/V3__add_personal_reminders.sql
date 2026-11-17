-- Existing notes remain intact. Unassigned legacy notes are not shown in personal lists.
ALTER TABLE tb_notes ADD COLUMN user_id uuid REFERENCES tb_users(id);
ALTER TABLE tb_notes ADD COLUMN scheduled_at timestamp(6) with time zone;
ALTER TABLE tb_notes ADD COLUMN completed boolean NOT NULL DEFAULT false;
CREATE INDEX ix_tb_notes_user_schedule ON tb_notes(user_id, scheduled_at);
