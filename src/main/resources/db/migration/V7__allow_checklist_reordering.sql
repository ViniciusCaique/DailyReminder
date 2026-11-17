-- Preserve the original V6 and move the checklist constraint correction forward.
-- The original UNIQUE(id) constraint is immediate, which prevents swapping steps.
ALTER TABLE tb_activity_checklist
    DROP CONSTRAINT tb_activity_checklist_id_key;
ALTER TABLE tb_activity_checklist
    ADD CONSTRAINT ux_activity_checklist_id UNIQUE (id)
    DEFERRABLE INITIALLY DEFERRED;
