ALTER TABLE chapter ADD summary TEXT NULL;
ALTER TABLE chapter DROP COLUMN narrative;
ALTER TABLE chapter_source DROP COLUMN relevance;
ALTER TABLE chapter_source DROP COLUMN contrast;
