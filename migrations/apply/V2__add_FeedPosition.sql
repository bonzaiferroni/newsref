ALTER TABLE lead_job ADD feed_position INT NULL;
ALTER TABLE feed ALTER COLUMN created_at TYPE TIMESTAMP, ALTER COLUMN created_at SET DEFAULT (NOW());
