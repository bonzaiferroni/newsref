ALTER TABLE source_score ADD link_id BIGINT NOT NULL;
ALTER TABLE source_score ADD CONSTRAINT fk_source_score_link_id__id FOREIGN KEY (link_id) REFERENCES link(id) ON DELETE CASCADE ON UPDATE RESTRICT;
