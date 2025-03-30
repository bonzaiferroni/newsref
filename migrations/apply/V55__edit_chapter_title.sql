ALTER TABLE chapter ADD title_huddle_id BIGINT NULL;
ALTER TABLE chapter ADD CONSTRAINT fk_chapter_title_huddle_id__id FOREIGN KEY (title_huddle_id) REFERENCES huddle(id) ON DELETE SET NULL ON UPDATE RESTRICT;
