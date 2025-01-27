CREATE TABLE IF NOT EXISTS story (id BIGSERIAL PRIMARY KEY, title TEXT NOT NULL, description TEXT NOT NULL);
CREATE TABLE IF NOT EXISTS chapter (id BIGSERIAL PRIMARY KEY, story_id BIGINT NOT NULL, title TEXT NOT NULL, narrative TEXT NOT NULL, created_at TIMESTAMP NOT NULL, score INT NOT NULL, average vector(384) NOT NULL, CONSTRAINT fk_chapter_story_id__id FOREIGN KEY (story_id) REFERENCES story(id) ON DELETE CASCADE ON UPDATE RESTRICT);
CREATE INDEX chapter_story_id ON chapter (story_id);
CREATE INDEX chapter_created_at ON chapter (created_at);
CREATE TABLE IF NOT EXISTS chapter_source (id BIGSERIAL PRIMARY KEY, chapter_id BIGINT NOT NULL, source_id BIGINT NOT NULL, relevance TEXT NOT NULL, contrast TEXT NOT NULL, "type" INT NOT NULL, CONSTRAINT fk_chapter_source_chapter_id__id FOREIGN KEY (chapter_id) REFERENCES chapter(id) ON DELETE CASCADE ON UPDATE RESTRICT, CONSTRAINT fk_chapter_source_source_id__id FOREIGN KEY (source_id) REFERENCES "source"(id) ON DELETE CASCADE ON UPDATE RESTRICT);
CREATE INDEX chapter_source_chapter_id ON chapter_source (chapter_id);
CREATE INDEX chapter_source_source_id ON chapter_source (source_id);
CREATE SEQUENCE IF NOT EXISTS chapter_id_seq START WITH 1 MINVALUE 1 MAXVALUE 9223372036854775807;
CREATE SEQUENCE IF NOT EXISTS chapter_source_id_seq START WITH 1 MINVALUE 1 MAXVALUE 9223372036854775807;
