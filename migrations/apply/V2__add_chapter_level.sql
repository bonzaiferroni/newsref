CREATE TABLE IF NOT EXISTS chapter_person (chapter_id BIGINT, person_id INT, CONSTRAINT pk_chapter_person PRIMARY KEY (chapter_id, person_id));
CREATE INDEX chapter_person_chapter_id ON chapter_person (chapter_id);
CREATE INDEX chapter_person_person_id ON chapter_person (person_id);
ALTER TABLE chapter_person ADD CONSTRAINT fk_chapter_person_chapter_id__id FOREIGN KEY (chapter_id) REFERENCES chapter(id) ON DELETE CASCADE ON UPDATE RESTRICT;
ALTER TABLE chapter_person ADD CONSTRAINT fk_chapter_person_person_id__id FOREIGN KEY (person_id) REFERENCES person(id) ON DELETE CASCADE ON UPDATE RESTRICT;
ALTER TABLE chapter ADD "level" INT DEFAULT 0 NOT NULL;
