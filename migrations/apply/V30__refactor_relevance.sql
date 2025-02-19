ALTER TABLE chapter_source ADD relevance INT NULL;
ALTER TABLE chapter_source DROP COLUMN is_relevant;
ALTER TABLE chapter_source ADD CONSTRAINT unique_chapter_source UNIQUE (chapter_id, source_id);
