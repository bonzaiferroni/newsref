ALTER TABLE feed_source ADD source_id BIGINT NOT NULL;
ALTER TABLE feed_source ADD checked_at TIMESTAMP NOT NULL;
ALTER TABLE feed_source ADD CONSTRAINT fk_feed_source_source_id__id FOREIGN KEY (source_id) REFERENCES "source"(id) ON DELETE RESTRICT ON UPDATE RESTRICT;
