CREATE TABLE IF NOT EXISTS source_note (id BIGSERIAL PRIMARY KEY, source_id BIGINT NOT NULL, note_id BIGINT NOT NULL, CONSTRAINT fk_source_note_source_id__id FOREIGN KEY (source_id) REFERENCES "source"(id) ON DELETE CASCADE ON UPDATE RESTRICT, CONSTRAINT fk_source_note_note_id__id FOREIGN KEY (note_id) REFERENCES note(id) ON DELETE SET NULL ON UPDATE RESTRICT);
CREATE INDEX source_note_source_id ON source_note (source_id);
CREATE INDEX source_note_note_id ON source_note (note_id);
CREATE SEQUENCE IF NOT EXISTS source_note_id_seq START WITH 1 MINVALUE 1 MAXVALUE 9223372036854775807;
