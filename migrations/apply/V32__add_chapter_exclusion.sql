CREATE TABLE IF NOT EXISTS chapter_exclusion (id BIGSERIAL PRIMARY KEY, chapter_id BIGINT NOT NULL, source_id BIGINT NOT NULL);
CREATE INDEX chapter_exclusion_chapter_id ON chapter_exclusion (chapter_id);
CREATE INDEX chapter_exclusion_source_id ON chapter_exclusion (source_id);
ALTER TABLE chapter_exclusion ADD CONSTRAINT chapter_exclusion_chapter_id_source_id_unique UNIQUE (chapter_id, source_id);
ALTER TABLE chapter_exclusion ADD CONSTRAINT fk_chapter_exclusion_chapter_id__id FOREIGN KEY (chapter_id) REFERENCES chapter(id) ON DELETE CASCADE ON UPDATE RESTRICT;
ALTER TABLE chapter_exclusion ADD CONSTRAINT fk_chapter_exclusion_source_id__id FOREIGN KEY (source_id) REFERENCES "source"(id) ON DELETE CASCADE ON UPDATE RESTRICT;
CREATE SEQUENCE IF NOT EXISTS chapter_exclusion_id_seq START WITH 1 MINVALUE 1 MAXVALUE 9223372036854775807;
ALTER TABLE chapter_source ADD CONSTRAINT chapter_source_chapter_id_source_id_unique UNIQUE (chapter_id, source_id);
