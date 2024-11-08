ALTER TABLE "source" ADD note_id BIGINT NULL;
CREATE INDEX source_note_id ON "source" (note_id);
ALTER TABLE "source" ADD CONSTRAINT fk_source_note_id__id FOREIGN KEY (note_id) REFERENCES note(id) ON DELETE SET NULL ON UPDATE RESTRICT;
