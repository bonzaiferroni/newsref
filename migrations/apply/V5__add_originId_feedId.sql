ALTER TABLE source_score ADD origin_id BIGINT NULL;
ALTER TABLE source_score ADD feed_id INT NULL;
ALTER TABLE feed ALTER COLUMN created_at TYPE TIMESTAMP, ALTER COLUMN created_at SET DEFAULT (NOW());
ALTER TABLE source_score ADD CONSTRAINT fk_source_score_origin_id__id FOREIGN KEY (origin_id) REFERENCES "source"(id) ON DELETE CASCADE ON UPDATE RESTRICT;
ALTER TABLE source_score ADD CONSTRAINT fk_source_score_feed_id__id FOREIGN KEY (feed_id) REFERENCES feed(id) ON DELETE CASCADE ON UPDATE RESTRICT;
