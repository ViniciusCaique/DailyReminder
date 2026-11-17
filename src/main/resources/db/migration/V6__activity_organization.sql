CREATE TABLE tb_activity_project (
 id uuid PRIMARY KEY, user_id uuid NOT NULL REFERENCES tb_users(id), name varchar(80) NOT NULL
);
CREATE UNIQUE INDEX ux_activity_project_name ON tb_activity_project(user_id, lower(name));
CREATE TABLE tb_activity_label (
 id uuid PRIMARY KEY, user_id uuid NOT NULL REFERENCES tb_users(id), name varchar(80) NOT NULL
);
CREATE UNIQUE INDEX ux_activity_label_name ON tb_activity_label(user_id, lower(name));
ALTER TABLE tb_activity ADD COLUMN project_id uuid REFERENCES tb_activity_project(id) ON DELETE SET NULL;
ALTER TABLE tb_activity ADD COLUMN version bigint NOT NULL DEFAULT 0;
CREATE INDEX ix_activity_project ON tb_activity(project_id);
CREATE TABLE tb_activity_labels (
 activity_id uuid NOT NULL REFERENCES tb_activity(id) ON DELETE CASCADE,
 label_id uuid NOT NULL REFERENCES tb_activity_label(id) ON DELETE CASCADE,
 PRIMARY KEY(activity_id, label_id)
);
CREATE TABLE tb_activity_checklist (
 activity_id uuid NOT NULL REFERENCES tb_activity(id) ON DELETE CASCADE,
 item_order integer NOT NULL, id uuid NOT NULL UNIQUE, text varchar(255) NOT NULL,
 completed boolean NOT NULL DEFAULT false,
 PRIMARY KEY(activity_id, item_order)
);
