ALTER TABLE feed ALTER COLUMN created_at TYPE TIMESTAMP, ALTER COLUMN created_at SET DEFAULT (NOW());
ALTER TABLE source_score DROP COLUMN link_id;
