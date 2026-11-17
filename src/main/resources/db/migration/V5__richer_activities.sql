ALTER TABLE tb_activity ADD COLUMN description varchar(10000);
ALTER TABLE tb_activity ADD COLUMN due_date date;
ALTER TABLE tb_activity ADD COLUMN status varchar(20);
UPDATE tb_activity SET status = CASE WHEN completed THEN 'DONE' ELSE 'TODO' END;
ALTER TABLE tb_activity ALTER COLUMN status SET NOT NULL;
ALTER TABLE tb_activity ALTER COLUMN status SET DEFAULT 'INBOX';
ALTER TABLE tb_activity ADD CONSTRAINT ck_activity_status CHECK (status IN ('INBOX', 'TODO', 'IN_PROGRESS', 'DONE'));
ALTER TABLE tb_activity ALTER COLUMN duration DROP NOT NULL;
ALTER TABLE tb_activity ADD CONSTRAINT ck_activity_duration CHECK (duration IS NULL OR duration BETWEEN 1 AND 1440);
-- Status is the source of truth; the API retains a derived completed field for compatibility.
ALTER TABLE tb_activity DROP COLUMN completed;
